package com.skyd.imomoe.util.update

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.Util.showToast


class UpdateNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val UPDATE_NOTIFICATION_ID = "UpdateNotificationID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: ""

        val notificationId = intent?.getIntExtra(UPDATE_NOTIFICATION_ID, -1) ?: -1

        when (action) {
            "notification_canceled" -> {
                if (notificationId != -1) {
                    val notificationManager =
                        App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    AppUpdateModel.status.postValue(AppUpdateStatus.CANCEL)
                    notificationManager.cancel(notificationId)
                    "取消下载".showToast()
                }
            }
            "download_clicked" -> {

            }
        }
    }
}