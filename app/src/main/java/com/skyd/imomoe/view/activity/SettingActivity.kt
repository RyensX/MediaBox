package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivitySettingBinding
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.viewmodel.SettingViewModel


class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private val viewModel: SettingViewModel by lazy { ViewModelProvider(this).get(SettingViewModel::class.java) }
    private var selfUpdateCheck = false
    private var changeNightMode = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appUpdateHelper = AppUpdateHelper.instance

        mBinding.run {
            llSettingActivityToolbar.tvToolbar1Title.text = getString(R.string.setting)
            llSettingActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }

            tvSettingActivityDownloadPathInfo.isFocused = true
            tvSettingActivityDownloadPathInfo.text = Const.DownloadAnime.animeFilePath
        }

        // 清理历史记录
        viewModel.mldDeleteAllHistory.observe(this, Observer {
            if (it) getString(R.string.delete_all_history_succeed).showToast()
            else getString(R.string.delete_all_history_failed).showToast()
        })
        mBinding.tvSettingActivityDeleteAllHistoryInfo.isFocused = true
        mBinding.rlSettingActivityDeleteAllHistory.setOnClickListener {
            MaterialDialog(this).show {
                icon(R.drawable.ic_delete_main_color_2_24)
                title(text = "警告")
                message(text = "确定要删除所有历史记录？包括搜索历史和观看历史")
                positiveButton(text = "删除") { viewModel.deleteAllHistory() }
                negativeButton(text = "取消") { dismiss() }
            }
        }

        // 清理缓存文件
        viewModel.mldCacheSize.observe(this, Observer {
            mBinding.tvSettingActivityClearCacheSize.text = it
        })
        viewModel.mldClearAllCache.observe(this, Observer {

            viewModel.getCacheSize()
            if (it) getString(R.string.clear_cache_succeed).showToast()
            else getString(R.string.clear_cache_failed).showToast()
        })
        viewModel.getCacheSize()
        mBinding.tvSettingActivityClearCache.isFocused = true
        mBinding.rlSettingActivityClearCache.setOnClickListener {
            MaterialDialog(this).show {
                icon(R.drawable.ic_sd_storage_main_color_2_24)
                title(text = "警告")
                message(text = "确定清理所有缓存？不包括缓存视频")
                positiveButton(text = "清理") { viewModel.clearAllCache() }
                negativeButton(text = "取消") { dismiss() }
            }
        }

        mBinding.run {
            tvSettingActivityUpdateInfo.text =
                getString(R.string.current_version, getAppVersionName())

            appUpdateHelper.getUpdateServer().observe(this@SettingActivity, Observer {
                tvSettingActivityUpdateInfoServer.text = AppUpdateHelper.serverName[it]
            })
            tvSettingActivityUpdateInfoServer.text =
                AppUpdateHelper.serverName[appUpdateHelper.getUpdateServer().value ?: 0]

            appUpdateHelper.getUpdateStatus().observe(this@SettingActivity, Observer {
                when (it) {
                    AppUpdateStatus.UNCHECK -> {
                        tvSettingActivityUpdateInfo.text = "未检查"
//                    appUpdateHelper.checkUpdate()
                    }
                    AppUpdateStatus.CHECKING -> {
                        tvSettingActivityUpdateTip.text = "正在检查更新..."
                    }
                    AppUpdateStatus.DATED -> {
                        tvSettingActivityUpdateTip.text = "发现新版本"
                        if (selfUpdateCheck) appUpdateHelper.noticeUpdate(this@SettingActivity)
                    }
                    AppUpdateStatus.VALID -> {
                        tvSettingActivityUpdateTip.text = "已是最新版本"
                        if (selfUpdateCheck) "已是最新版本".showToast()
                    }
                    AppUpdateStatus.LATER -> {
                        tvSettingActivityUpdateTip.text = "暂不更新"
                    }
                    AppUpdateStatus.DOWNLOADING -> {
                        tvSettingActivityUpdateTip.text = "新版本下载中..."
                    }
                    AppUpdateStatus.CANCEL -> {
                        tvSettingActivityUpdateTip.text = "下载被取消"
                    }
                    AppUpdateStatus.TO_BE_INSTALLED -> {
                        tvSettingActivityUpdateTip.text = "待安装"
                        if (selfUpdateCheck) appUpdateHelper.installUpdate(this@SettingActivity)
                    }
                    AppUpdateStatus.ERROR -> {
                        tvSettingActivityUpdateTip.text = "更新失败"
                        if (selfUpdateCheck) "获取更新失败！".showToast()
                    }
                    else -> return@Observer
                }
            })
        }

        mBinding.rlSettingActivityUpdate.setOnClickListener {
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

        mBinding.rlSettingActivityUpdateServer.setOnClickListener {
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

        mBinding.switchSettingActivityNightMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!changeNightMode) {
                changeNightMode = true
                return@setOnCheckedChangeListener
            }
            if (isChecked) {
                //夜间
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mBinding.tvSettingActivityNightModeInfo.text = "夜间"
            } else {
                //日间
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mBinding.tvSettingActivityNightModeInfo.text = "白天"
            }
            App.context.sharedPreferences("nightMode").editor {
                putBoolean("isNightMode", isChecked)
            }
        }

        mBinding.run {
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> {
                    tvSettingActivityNightModeInfo.text = "夜间"
                    if (!switchSettingActivityNightMode.isChecked) {
                        changeNightMode = false
                        switchSettingActivityNightMode.isChecked = true
                    }
                }
                else -> {
                    tvSettingActivityNightModeInfo.text = "白天"
                    if (switchSettingActivityNightMode.isChecked) {
                        changeNightMode = false
                        switchSettingActivityNightMode.isChecked = false
                    }
                }
            }
        }
    }

    override fun getBinding(): ActivitySettingBinding =
        ActivitySettingBinding.inflate(layoutInflater)
}
