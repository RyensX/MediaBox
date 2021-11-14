package com.skyd.imomoe.util.downloadanime

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const.DownloadAnime.Companion.animeFilePath
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.downloadHashMap
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.save2Xml
import com.skyd.imomoe.util.downloadanime.AnimeDownloadNotificationReceiver.Companion.DOWNLOAD_ANIME_NOTIFICATION_ID
import com.skyd.imomoe.util.toMD5
import com.skyd.imomoe.view.activity.MainActivity
import kotlinx.coroutines.*
import java.io.File
import java.io.Serializable


class AnimeDownloadService : Service() {
    private val downloadServiceHashMap: HashMap<String, AnimeDownloadServiceDataBean> = HashMap()
    private val folderAndFileNameHashMap: HashMap<String, String> = HashMap()

    private var notificationManager: NotificationManager? = null
    private var totalNotificationId = 1002
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.extras == null) {
            coroutineScope.cancel()
            "取消下载".showToast()
            return START_NOT_STICKY
        }
        val url = intent.getStringExtra("url") ?: ""
        val key = intent.getStringExtra("key") ?: ""
        val folderAndFileName = intent.getStringExtra("folderAndFileName") ?: ""
        folderAndFileNameHashMap[key] = folderAndFileName
        downloadServiceHashMap[key] = AnimeDownloadServiceDataBean(url, totalNotificationId++)
        if (isNetWorkAvailable()) {
            createNotification(key)
            downloadAnime(key, url, object : DownloadListener {
                override fun complete(fileName: String) {
                    deleteNotification(key)
                    val animeDir = folderAndFileName.split("/").first()
                    val title = folderAndFileName.split("/").last()
                    val file = File("$animeFilePath$animeDir/$fileName")
                    if (file.exists()) {
                        downloadHashMap[key]?.postValue(AnimeDownloadStatus.COMPLETE)
                        GlobalScope.launch(Dispatchers.IO) {
                            file.toMD5()?.let {
                                val entity = AnimeDownloadEntity(it, title, fileName)
                                getAppDataBase().animeDownloadDao().insertAnimeDownload(entity)
                                save2Xml(animeDir, entity)
                            }
                        }
                        "${folderAndFileName}下载完成".showToast()
                    } else {
                        if (downloadHashMap[key]?.value != AnimeDownloadStatus.CANCEL) {
                            downloadHashMap[key]?.postValue(
                                AnimeDownloadStatus.ERROR
                            )
                        }
                        "文件未找到，下载失败".showToast()
                    }
                }

                override fun error() {
                    super.error()
                    deleteNotification(key)
                    downloadHashMap[key]?.postValue(AnimeDownloadStatus.ERROR)
                }
            })
            "开始下载${folderAndFileName}...".showToast()
        }
        return START_NOT_STICKY
    }

    private fun deleteNotification(key: String) {
        downloadServiceHashMap[key]?.let {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(it.notificationId)
        }
    }

    @SuppressLint("MissingPermission")
    private fun isNetWorkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && activeNetInfo.isConnected
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        for (key in downloadServiceHashMap.keys) {
            downloadHashMap[key]?.postValue(AnimeDownloadStatus.CANCEL)
            val file = File(animeFilePath + key)
            if (file.exists()) {
                file.delete()
            }
        }
        downloadServiceHashMap.clear()
    }

    private fun createNotification(key: String) {
        val folderAndFileName = folderAndFileNameHashMap[key]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            downloadServiceHashMap[key]?.builder = NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            downloadServiceHashMap[key]?.builder = NotificationCompat.Builder(this)
        }
        val notificationId = downloadServiceHashMap[key]?.notificationId ?: -1
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent(this, AnimeDownloadNotificationReceiver::class.java)
        stopIntent.action = "notification_canceled"
        stopIntent.putExtra(DOWNLOAD_ANIME_NOTIFICATION_ID, notificationId)
        stopIntent.putExtra("key", key)

        val clickIntent = Intent(Intent.ACTION_MAIN)
        clickIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        clickIntent.setClass(this, MainActivity::class.java)
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        clickIntent.putExtra(DOWNLOAD_ANIME_NOTIFICATION_ID, notificationId)
        downloadServiceHashMap[key]?.builder?.setSmallIcon(R.mipmap.ic_launcher)
            ?.setContentTitle("正在下载$folderAndFileName")
            ?.setContentText("0%")
            ?.setProgress(100, 0, false)
            ?.setDeleteIntent(
                PendingIntent.getBroadcast(
                    this,
                    //requestCode需要不一样才能接收每次的消息
                    notificationId,
                    stopIntent,
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT
                    else PendingIntent.FLAG_MUTABLE
                )
            )?.setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    clickIntent,
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT
                    else PendingIntent.FLAG_MUTABLE
                )
            )?.setAutoCancel(false)?.setTicker(folderAndFileName)
        val notification = downloadServiceHashMap[key]?.builder?.build()
        notificationManager?.notify(notificationId, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(key: String, progress: Int) {
        val n = downloadServiceHashMap[key]?.builder ?: return

        n.setProgress(100, progress, false)
        n.setContentText("$progress%")

        val manager = notificationManager
            ?: getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = n.build()
        manager.notify(downloadServiceHashMap[key]?.notificationId ?: -1, notification)
        if (notificationManager == null) {
            notificationManager = manager
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun downloadAnime(
        key: String,
        param: String,
        listener: DownloadListener
    ) {
        val animeDir = folderAndFileNameHashMap[key]?.split("/")?.first()
        downloadHashMap[key]?.postValue(AnimeDownloadStatus.DOWNLOADING)
        FileDownloader.getImpl().create(param)
            .setPath(animeFilePath + animeDir, true)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (downloadHashMap[key]?.value == AnimeDownloadStatus.CANCEL) {
                        task?.let {
                            FileDownloader.getImpl().pause(it.id)
                        }
                    }
                    val progress = (soFarBytes.toFloat() / totalBytes * 100).toInt()
                    onProgressUpdate(key, progress)
                }

                override fun completed(task: BaseDownloadTask?) {
                    listener.complete(task?.filename ?: "")
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    listener.error()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    downloadHashMap[key]?.postValue(AnimeDownloadStatus.ERROR)
                    e?.printStackTrace()
                    e?.message?.showToast(Toast.LENGTH_LONG)
                    listener.error()
                }

                override fun warn(task: BaseDownloadTask?) {
                }
            }).start()
    }

    private fun onProgressUpdate(key: String, values: Int) {
        updateNotification(key, values)
    }

    inner class AnimeDownloadServiceDataBean(
        var url: String,
        var notificationId: Int,
        var builder: NotificationCompat.Builder? = null
    ) : Serializable

    companion object {
        const val CHANNEL_ID = "download_anime"
        const val CHANNEL_NAME = "下载消息"
    }
}
