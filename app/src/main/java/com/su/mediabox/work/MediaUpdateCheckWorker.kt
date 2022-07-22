package com.su.mediabox.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.work.*
import com.su.mediabox.App
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.plugin.MediaUpdateCheck
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.acquireComponent
import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent
import com.su.mediabox.util.Util
import com.su.mediabox.util.logD
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import com.su.mediabox.R
import com.su.mediabox.util.getDataFormat
import com.su.mediabox.util.toLiveData
import com.su.mediabox.view.activity.MainActivity
import java.util.*

const val MEDIA_UPDATE_CHECK_WORKER_ID = "MEDIA_UPDATE_CHECK_WORKER_ID"
const val MEDIA_UPDATE_CHECK_WORKER_TAG = "MEDIA_UPDATE_CHECK_WORKER_TAG"
val mediaUpdateCheckWorkerIsRunning = WorkManager.getInstance(App.context)
    .getWorkInfosByTagLiveData(MEDIA_UPDATE_CHECK_WORKER_TAG)
    .map { list ->
        list.find { it.state == WorkInfo.State.RUNNING } != null
    }

internal class MediaUpdateCheckWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    private val mediaUpdateNofChannelId = "media_update_check"
    private val TAG = "媒体检查更新Worker"

    private fun createForegroundInfo(): ForegroundInfo {
        val cancel = applicationContext.getString(R.string.cancel)
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, mediaUpdateNofChannelId)
            .setContentTitle(applicationContext.getString(R.string.media_update_check_name))
            .setContentText(applicationContext.getString(R.string.media_update_check_pref_now_summary))
            .setSmallIcon(R.mipmap.ic_mediabox)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(2, notification)
    }

    @FlowPreview
    override suspend fun doWork(): Result {
        if (mediaUpdateCheckWorkerIsRunning.value == true)
            return Result.failure()
        setForeground(createForegroundInfo())
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            logD(TAG, "发生错误:${throwable.message}")
        }) {
            PluginManager.pluginFlow.first().asFlow()
                //TODO 检查插件私有存储配置决定是否参与更新
                //TODO ？定时1小时更新，插件可通过私有键对存储配置参与的更新时段
                .flatMapMerge { plugin ->
                    //并行检查
                    flow {
                        Util.withoutExceptionGet {
                            plugin.acquireComponent(
                                IMediaUpdateDataComponent::class.java
                            )
//                            object : IMediaUpdateDataComponent {
//                                override suspend fun getUpdateTag(detailUrl: String): String =
//                                    getDataFormat("H:m").format(System.currentTimeMillis())
//                            }
                        }?.also { component ->
                            val favorites = plugin.getAppDataBase().favoriteDao().getFavoriteList()
                            MediaUpdateCheck.checkMediaUpdate(favorites, plugin, component) {
                                emit(Pair(plugin, it))
                            }
                        }
                    }
                }
                //.filter { it.second.isNotEmpty() }
                .toList().also { result ->
                    val validData = result.filter { it.second.isNotEmpty() }
                    createNotificationChannel()
                    logD(TAG, "共计检查${result.size}，发现${validData.size}个插件的媒体有更新")

                    validData.forEach {
                        val updateList = NotificationCompat.InboxStyle()
                        it.second.take(5).forEachIndexed { index, media ->
                            media?.apply {
                                updateList.addLine("${index + 1}.$targetMediaLabel : $newTag")
                            }
                        }
                        if (it.second.size > 5)
                            updateList.addLine("...")

                        val pluginMediaUpdateNofBuilder =
                            NotificationCompat.Builder(applicationContext, mediaUpdateNofChannelId)
                                .apply {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        setSmallIcon(IconCompat.createWithBitmap(it.first.icon.toBitmap()))
                                    } else
                                        setSmallIcon(R.mipmap.ic_mediabox)
                                }
                                .setContentTitle(it.first.name)
                                .setContentText(
                                    applicationContext.getString(
                                        R.string.media_update_check_plugin_result,
                                        it.second.size
                                    )
                                )
                                .setStyle(updateList)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setGroup(mediaUpdateNofChannelId)

                        with(NotificationManagerCompat.from(applicationContext)) {
                            notify(it.first.hashCode(), pluginMediaUpdateNofBuilder.build())
                        }
                    }

                    val notifyIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val notifyPendingIntent = PendingIntent.getActivity(
                        applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val mediaUpdateCheckNofBuilder =
                        NotificationCompat.Builder(applicationContext, mediaUpdateNofChannelId)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    applicationContext.resources,
                                    R.mipmap.ic_mediabox
                                )
                            )
                            .setSmallIcon(R.mipmap.ic_mediabox)
                            .setContentTitle(applicationContext.getString(R.string.media_update_check_name))
                            .setContentText(
                                applicationContext.getString(
                                    R.string.media_update_check_result,
                                    result.size, validData.size
                                )
                            )
                            .setContentIntent(notifyPendingIntent)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(applicationContext.getString(R.string.media_update_check_hint))
                            )
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                    with(NotificationManagerCompat.from(applicationContext)) {
                        notify(1, mediaUpdateCheckNofBuilder.build())
                    }
                }

        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.media_update_check_name)
            val descriptionText = applicationContext.getString(R.string.media_update_check_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(mediaUpdateNofChannelId, name, importance).apply {
                description = descriptionText
            }
            NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
        }
    }

}

fun launchMediaUpdateCheckWorker() {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)//电量充足才检查
        .build()

    val request = PeriodicWorkRequestBuilder<MediaUpdateCheckWorker>(24, TimeUnit.HOURS)
        .addTag(MEDIA_UPDATE_CHECK_WORKER_TAG)
        .setConstraints(constraints)
        //TODO 差异延迟+定时
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        //.setInitialDelay(1, TimeUnit.HOURS)
        .build()

    WorkManager.getInstance(App.context).enqueueUniquePeriodicWork(
        MEDIA_UPDATE_CHECK_WORKER_ID,
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}

fun launchMediaUpdateCheckWorkerNow() {
    val request = OneTimeWorkRequestBuilder<MediaUpdateCheckWorker>()
        .addTag(MEDIA_UPDATE_CHECK_WORKER_TAG)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()

    WorkManager.getInstance(App.context)
        .enqueueUniqueWork(
            MediaUpdateCheckWorker::class.java.name,
            ExistingWorkPolicy.KEEP,
            request
        )

}