package com.skyd.imomoe.util.update

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skyd.imomoe.config.Const.Update.Companion.updateFile
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.uri
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class AppUpdateHelper private constructor() {
    companion object {
        private var instance: AppUpdateHelper? = null

        fun getInstance(): AppUpdateHelper {
            instance?.let {
                return it
            }
            val ins = AppUpdateHelper()
            instance = ins
            return ins
        }
    }

    fun getUpdateStatus(): LiveData<AppUpdateStatus> = AppUpdateModel.status

    fun checkUpdate() {
        AppUpdateModel.checkUpdate()
    }

    fun noticeUpdate(activity: AppCompatActivity) {
        val updateBean = AppUpdateModel.updateBean ?: return
        MaterialDialog(activity).show {
            title(text = "发现新版本")
            message(text = "最新版本：" + updateBean.name + "\n" + updateBean.body)
            positiveButton(text = "更新") {
                XXPermissions.with(activity).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            downloadUpdate(activity)
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            super.onDenied(permissions, never)
                            "无存储权限，无法下载更新！".showToast()
                        }
                    })
            }
            negativeButton(text = "取消") {
                dismiss()
                AppUpdateModel.status.value = AppUpdateStatus.LATER
            }
            cornerRadius(literalDp = 7f)
        }
    }

    @SuppressLint("CheckResult")
    fun installUpdate(activity: AppCompatActivity) {
        requestPackageInstallPermissions(activity).subscribe {
            if (it != true) {
                return@subscribe
            }
            try {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .addFlags(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            } else {
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        .setDataAndType(updateFile.uri, "application/vnd.android.package-archive")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                AppUpdateModel.status.value = AppUpdateStatus.ERROR
            }
        }
    }

    private fun downloadUpdate(activity: AppCompatActivity) {
        AppUpdateModel.status.value = AppUpdateStatus.DOWNLOADING
        activity.startService(
            Intent(activity, AppUpdateDownloadService::class.java)
                .putExtra(
                    "url",
                    AppUpdateModel.updateBean?.assets?.get(0)?.browserDownloadUrl ?: return
                )
        )
    }

    private fun requestPackageInstallPermissions(activity: AppCompatActivity): Observable<Boolean> {
        var fragment: PackageInstallsFragment?
        if (activity.isFinishing) {
            return Observable.just(false)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || activity.packageManager.canRequestPackageInstalls()) {
            return Observable.just(true)
        }

        val fm = activity.supportFragmentManager
        fragment = fm.findFragmentByTag(PackageInstallsFragment.TAG) as PackageInstallsFragment?
        if (fragment == null) {
            fragment = PackageInstallsFragment()
            fm.beginTransaction()
                .add(fragment, PackageInstallsFragment.TAG)
                .commitNow()
        }
        if (!fragment.requesting) {
            fragment.sub = PublishSubject.create()
        }
        fragment.requestPermissions()

        return fragment.sub ?: Observable.just(false)
    }
}