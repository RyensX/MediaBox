package com.su.mediabox.util

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleCoroutineScope
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.App
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.net.RetrofitManager
import com.su.mediabox.net.service.AppService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Keep
data class Announcement(val version: Int, val time: String? = null, val content: String)

private val api by lazy { RetrofitManager.get().create(AppService::class.java) }

fun checkAnnouncement(context: Context, lifecycleCoroutineScope: LifecycleCoroutineScope) {
    lifecycleCoroutineScope.launch(Dispatchers.Main + CoroutineExceptionHandler { _, throwable ->
        logD("公告获取失败", throwable.message ?: "")
        throwable.printStackTrace()
    }) {
        val announcement = withContext(Dispatchers.IO) { api.getAnnouncement() }
        if (announcement.version > Pref.announcementVersion.value) {
            logD("更新公告", announcement.toString())
            Pref.announcementVersion.saveData(announcement.version)
            announcementDialog(context, announcement)
        }
    }
}

private fun announcementDialog(context: Context, announcement: Announcement) {
    val msg = StringBuilder()
    if (!announcement.time.isNullOrBlank())
        msg.append(announcement.time).append("\n")
    msg.append(announcement.content)
    MaterialDialog(context).show {
        title(res = R.string.announcement_title)
        message(text = msg.toString())
        positiveButton(res = R.string.ok) { }
    }
}