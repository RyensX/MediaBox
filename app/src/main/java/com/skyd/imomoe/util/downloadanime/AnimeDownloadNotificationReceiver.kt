package com.skyd.imomoe.util.downloadanime

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.downloadHashMap


class AnimeDownloadNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val DOWNLOAD_ANIME_NOTIFICATION_ID = "DownloadAnimeNotificationID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: ""

        val notificationId = intent?.getIntExtra(DOWNLOAD_ANIME_NOTIFICATION_ID, -1) ?: -1
        val key = intent?.getStringExtra("key") ?: ""

        when (action) {
            "notification_canceled" -> {
                if (notificationId != -1) {
                    val notificationManager =
                        App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                    downloadHashMap[key]?.postValue(AnimeDownloadStatus.CANCEL)
                    "取消下载".showToast()
                }
            }
        }
    }
}