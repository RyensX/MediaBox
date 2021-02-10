package com.skyd.imomoe.util.update

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const.Update.Companion.updateFile
import com.skyd.imomoe.config.Const.Update.Companion.updateFilePath
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.util.update.UpdateNotificationReceiver.Companion.UPDATE_NOTIFICATION_ID
import com.skyd.imomoe.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class AppUpdateDownloadService : Service() {

    private var notificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val notificationId = 1001
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.extras == null) {
            coroutineScope.cancel()
            "取消更新".showToast()
            return START_NOT_STICKY
        }
        val url = intent.getStringExtra("url") ?: ""
        if (url.isBlank()) {
            stopSelf()
        } else if (isNetWorkAvailable()) {
            createNotification()
            coroutineScope.launch {
                val downloadResult = downloadApk(url)
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancel(notificationId)
                if (downloadResult) {
                    val file = updateFile
                    if (file.exists()) {
                        AppUpdateModel.status.postValue(AppUpdateStatus.TO_BE_INSTALLED)
                    } else {
                        if (AppUpdateModel.status.value != AppUpdateStatus.CANCEL) {
                            AppUpdateModel.status.postValue(AppUpdateStatus.ERROR)
                        }
                        "安装包未找到，更新失败".showToast()
                    }
                }
            }
            "开始下载新版樱花动漫...".showToast()
        }
        return START_NOT_STICKY
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
        AppUpdateModel.status.postValue(AppUpdateStatus.CANCEL)
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopIntent = Intent(this, UpdateNotificationReceiver::class.java)
        stopIntent.action = "notification_canceled"
        stopIntent.putExtra(UPDATE_NOTIFICATION_ID, notificationId)

        val clickIntent = Intent(Intent.ACTION_MAIN)
        clickIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        clickIntent.setClass(this, MainActivity::class.java)
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        clickIntent.putExtra(UPDATE_NOTIFICATION_ID, notificationId)
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("新版" + App.context.getString(R.string.app_name) + "下载中...")
            .setContentText("0%")
            .setProgress(100, 0, false)
            .setDeleteIntent(
                PendingIntent.getBroadcast(
                    this,
                    0,
                    stopIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            )
            .setContentIntent(
                PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            )
            .setAutoCancel(false)
            .setTicker("正在下载新版" + App.context.getString(R.string.app_name) + "...")
        val notification = builder?.build()
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

    private fun updateNotification(progress: Int) {
        val n = builder ?: return

        n.setProgress(100, progress, false)
        n.setContentText("$progress%")

        val manager = notificationManager
            ?: getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = n.build()
        manager.notify(notificationId, notification)
        if (notificationManager == null) {
            notificationManager = manager
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun downloadApk(param: String): Boolean {
        var finish = false
        try {
            val url = URL(param)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            if (conn.responseCode == 200) {
                val f = File(updateFilePath)
                if (!f.isDirectory) {
                    f.mkdirs()
                }
                val `is` = conn.inputStream
                val length = conn.contentLength
                val file = updateFile
                if (file.exists()) {
                    file.delete()  // I think maybe the existent file cause the update failure
                }
                val fos = FileOutputStream(file)
                var count = 0
                val buf = ByteArray(1024)
                var progress: Int
                var progressPre = 0

                var numRead = `is`.read(buf)
                while (numRead > 0) {
                    if (AppUpdateModel.status.value == AppUpdateStatus.CANCEL) {
                        return false
                    }
                    count += numRead
                    progress = (count.toFloat() / length * 100).toInt()
                    if (progress != progressPre) {
                        onProgressUpdate(progress)
                        progressPre = progress
                    }
                    fos.write(buf, 0, numRead)
                    numRead = `is`.read(buf)
                }
                fos.flush()
                fos.close()
                `is`.close()
                finish = true
            } else {
                AppUpdateModel.status.postValue(AppUpdateStatus.ERROR)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish = false
            AppUpdateModel.status.postValue(AppUpdateStatus.ERROR)
            e.message?.showToastOnThread()
        }

        return finish
    }

    private fun onProgressUpdate(values: Int) {
        updateNotification(values)
    }

    companion object {
        const val CHANNEL_ID = "app_update"
        const val CHANNEL_NAME = "更新消息"
    }
}
