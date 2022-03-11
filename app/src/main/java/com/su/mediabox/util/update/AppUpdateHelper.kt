package com.su.mediabox.util.update

import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.su.mediabox.model.AppUpdateModel
import com.su.mediabox.util.Util.openBrowser
import com.su.mediabox.util.formatSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AppUpdateHelper private constructor() {
    companion object {
        const val UPDATE_SERVER_SP_KEY = "updateServer"
        const val GITHUB = 0
        val serverName = arrayOf("Github")

        val instance: AppUpdateHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppUpdateHelper()
        }
    }

    fun getUpdateServer(): LiveData<Int> = AppUpdateModel.mldUpdateServer

    fun setUpdateServer(value: Int) {
        AppUpdateModel.updateServer = value
    }

    fun getUpdateStatus(): LiveData<AppUpdateStatus> = AppUpdateModel.status

    fun checkUpdate() {
        //TODO 如果插件是在github开源的可以提供地址被宿主主动检查更新
        AppUpdateModel.checkUpdate()
    }

    fun noticeUpdate(activity: AppCompatActivity) {
        listOf<Function<Unit>> { checkUpdate() }
        val updateBean = AppUpdateModel.updateBean ?: return
        val isImportantUpdate = updateBean.name.contains("*")
        val name = updateBean.name.replace("*", "")
        MaterialDialog(activity)
            .cancelable(!isImportantUpdate)
            .show {
                title(text = "发现${if (isImportantUpdate) "重要" else ""}新版本\n版本名：${name}\n版本代号：${updateBean.tagName}")
                StringBuffer().apply {
                    val size = updateBean.assets[0].size
                    if (size > 0) {
                        append("<p>大小：${size.toDouble().formatSize()}<br/>")
                    }
                    val updatedAt = updateBean.assets[0].updatedAt
                    if (!updatedAt.isNullOrBlank()) {
                        try {
                            val format =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                            format.timeZone = TimeZone.getTimeZone("UTC")
                            val date = format.parse(updatedAt)
                            val s: String = if (date != null) {
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                                    date
                                )
                            } else {
                                updatedAt
                            }
                            append("发布于：${s}<br/>")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val downloadCount = updateBean.assets[0].downloadCount
                    if (!downloadCount.isNullOrBlank()) {
                        append("下载次数：${downloadCount}次<p/>")
                    }
                    append(updateBean.body)
                    this@show.message(
                        text = this.toString()
                    ) {
                        html()
                    }
                }
                val t = "下载更新"
                positiveButton(text = t) {
                    openBrowser(
                        AppUpdateModel.updateBean?.assets?.get(0)?.browserDownloadUrl
                            ?: return@positiveButton
                    )
                }
                if (!isImportantUpdate)
                    negativeButton(text = "取消") {
                        dismiss()
                        AppUpdateModel.status.value = AppUpdateStatus.LATER
                    }
                if (isImportantUpdate)
                    getActionButton(WhichButton.POSITIVE).apply {
                        isEnabled = false
                        activity.lifecycleScope.launch {
                            for (i in 15 downTo 0) {
                                delay(1000)
                                text = String.format("%s(%d)", t, i)
                            }
                            text = t
                            isEnabled = true
                        }
                    }
            }
    }
}