package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

        tv_toolbar_1_title.text = getString(R.string.setting)
        iv_toolbar_1_back.setOnClickListener { finish() }

        tv_setting_activity_download_path_info.isFocused = true
        tv_setting_activity_download_path_info.text = Const.DownloadAnime.animeFilePath

        tv_setting_activity_update_info.text =
            getString(R.string.current_version, getAppVersionName())

        val appUpdateHelper = AppUpdateHelper.instance
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
            val appUpdateService = AppUpdateHelper.instance
            when (appUpdateService.getUpdateStatus().value) {
                AppUpdateStatus.DOWNLOADING -> {
                    "正在下载新版本，下拉可以查看进度".showToast()
                }
                AppUpdateStatus.CANCEL -> {
                    appUpdateService.noticeUpdate(this)
                }
                AppUpdateStatus.CHECKING -> {
                    "已在检查，请稍等...".showToast()
                }
                AppUpdateStatus.TO_BE_INSTALLED -> {
                    appUpdateService.installUpdate(this)
                }
                else -> appUpdateService.checkUpdate()
            }
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