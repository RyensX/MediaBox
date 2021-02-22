package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*


class SettingActivity : AppCompatActivity() {
    private var selfUpdateCheck = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val appUpdateHelper = AppUpdateHelper.instance

        tv_toolbar_1_title.text = getString(R.string.setting)
        iv_toolbar_1_back.setOnClickListener { finish() }

        tv_setting_activity_download_path_info.isFocused = true
        tv_setting_activity_download_path_info.text = Const.DownloadAnime.animeFilePath

        tv_setting_activity_update_info.text =
            getString(R.string.current_version, getAppVersionName())

        appUpdateHelper.getUpdateServer().observe(this, {
            tv_setting_activity_update_info_server.text = AppUpdateHelper.serverName[it]
        })
        tv_setting_activity_update_info_server.text =
            AppUpdateHelper.serverName[appUpdateHelper.getUpdateServer().value ?: 0]

        appUpdateHelper.getUpdateStatus().observe(this, {
            when (it) {
                AppUpdateStatus.UNCHECK -> {
                    tv_setting_activity_update_tip.text = "未检查"
//                    appUpdateHelper.checkUpdate()
                }
                AppUpdateStatus.CHECKING -> {
                    tv_setting_activity_update_tip.text = "正在检查更新..."
                }
                AppUpdateStatus.DATED -> {
                    tv_setting_activity_update_tip.text = "发现新版本"
                    if (selfUpdateCheck) appUpdateHelper.noticeUpdate(this)
                }
                AppUpdateStatus.VALID -> {
                    tv_setting_activity_update_tip.text = "已是最新版本"
                    if (selfUpdateCheck) "已是最新版本".showToast()
                }
                AppUpdateStatus.LATER -> {
                    tv_setting_activity_update_tip.text = "暂不更新"
                }
                AppUpdateStatus.DOWNLOADING -> {
                    tv_setting_activity_update_tip.text = "新版本下载中..."
                }
                AppUpdateStatus.CANCEL -> {
                    tv_setting_activity_update_tip.text = "下载被取消"
                }
                AppUpdateStatus.TO_BE_INSTALLED -> {
                    tv_setting_activity_update_tip.text = "待安装"
                    if (selfUpdateCheck) appUpdateHelper.installUpdate(this)
                }
                AppUpdateStatus.ERROR -> {
                    tv_setting_activity_update_tip.text = "更新失败"
                    if (selfUpdateCheck) "获取更新失败！".showToast()
                }
                else -> return@observe
            }
        })

        rl_setting_activity_update.setOnClickListener {
            selfUpdateCheck = true
            when (appUpdateHelper.getUpdateStatus().value) {
                AppUpdateStatus.DOWNLOADING -> {
                    "正在下载新版本，下拉可以查看进度".showToast()
                }
//                AppUpdateStatus.CANCEL -> {
//                    appUpdateService.noticeUpdate(this)
//                }
                AppUpdateStatus.CHECKING -> {
                    "已在检查，请稍等...".showToast()
                }
                AppUpdateStatus.TO_BE_INSTALLED -> {
                    appUpdateHelper.installUpdate(this)
                }
                else -> appUpdateHelper.checkUpdate()
            }
        }

        rl_setting_activity_update_server.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.check_update_server)
            builder.setIcon(R.drawable.ic_storage_main_color_2_24)
            builder.setSingleChoiceItems(
                AppUpdateHelper.serverName, appUpdateHelper.getUpdateServer().value ?: 0
            ) { dialog, which ->
                if (which == 1)
                    "Gitee有请求次数的限制，可能会更新失败！\n由于第三方下载库存在BUG，通过Gitee服务器会下载失败。因此，只能通过浏览器下载"
                        .showToast(Toast.LENGTH_LONG)
                appUpdateHelper.setUpdateServer(which)
                dialog.dismiss()
            }
            builder.create().show()
        }

        rl_setting_activity_about.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutActivity::class.java
                )
            )
        }
    }
}