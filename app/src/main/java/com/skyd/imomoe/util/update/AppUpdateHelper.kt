package com.skyd.imomoe.util.update

import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.Util.openBrowser
import com.skyd.imomoe.util.formatSize
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
        AppUpdateModel.checkUpdate()
    }

    fun noticeUpdate(activity: AppCompatActivity) {
        listOf<Function<Unit>> { checkUpdate() }
        val updateBean = AppUpdateModel.updateBean ?: return
        MaterialDialog(activity).show {
            title(text = "发现新版本：${updateBean.name}")
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
                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
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
                    text = Html.fromHtml(this.toString())
                )
            }
            positiveButton(text = "下载更新") {
                openBrowser(
                    AppUpdateModel.updateBean?.assets?.get(0)?.browserDownloadUrl
                        ?: return@positiveButton
                )
            }
            negativeButton(text = "取消") {
                dismiss()
                AppUpdateModel.status.value = AppUpdateStatus.LATER
            }
        }
    }
}