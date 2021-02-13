package com.skyd.imomoe.util.downloadanime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skyd.imomoe.config.Const.DownloadAnime.Companion.animeFilePath
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.Util.showToast
import java.io.File

class AnimeDownloadHelper private constructor() {

    companion object {
        val downloadHashMap: HashMap<String, MutableLiveData<AnimeDownloadStatus>> = HashMap()
        val instance: AnimeDownloadHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AnimeDownloadHelper()
        }
    }

    fun getDownloadStatus(key: String): LiveData<AnimeDownloadStatus>? = downloadHashMap[key]

    fun downloadAnime(activity: AppCompatActivity, url: String, key: String) {
        XXPermissions.with(activity).permission(Permission.MANAGE_EXTERNAL_STORAGE).request(
            object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (downloadHashMap[key]?.value == AnimeDownloadStatus.DOWNLOADING) {
                        "已经在下载啦...".showToast()
                        return
                    } else if (downloadHashMap[key]?.value == AnimeDownloadStatus.COMPLETE) {
                        "已经下载好啦...".showToast()
                        return
                    }
                    val status = MutableLiveData<AnimeDownloadStatus>()
                    status.value = AnimeDownloadStatus.DOWNLOADING
                    downloadHashMap[key] = status
                    activity.startService(
                        Intent(activity, AnimeDownloadService::class.java)
                            .putExtra("url", url)
                            .putExtra("key", key)
                    )
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    "未获取存储权限，无法下载".showToast()
                }
            }
        )
    }
}