package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivitySettingBinding
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.restartApp
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.viewmodel.SettingViewModel
import com.skyd.skin.SkinManager
import kotlinx.coroutines.*
import java.net.URL


class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private val viewModel: SettingViewModel by lazy { ViewModelProvider(this).get(SettingViewModel::class.java) }
    private var selfUpdateCheck = false

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
            if (it == null) return@Observer
            if (it) getString(R.string.delete_all_history_succeed).showToast()
            else getString(R.string.delete_all_history_failed).showToast()
            viewModel.mldDeleteAllHistory.postValue(null)
        })
        mBinding.tvSettingActivityDeleteAllHistoryInfo.isFocused = true
        mBinding.rlSettingActivityDeleteAllHistory.setOnClickListener {
            MaterialDialog(this).show {
                icon(R.drawable.ic_delete_main_color_2_24_skin)
                title(res = R.string.warning)
                message(text = "确定要删除所有历史记录？包括搜索历史和观看历史")
                positiveButton(res = R.string.delete) { viewModel.deleteAllHistory() }
                negativeButton(res = R.string.cancel) { dismiss() }
            }
        }

        // 清理缓存文件
        viewModel.mldCacheSize.observe(this, Observer {
            mBinding.tvSettingActivityClearCacheSize.text = it
        })
        viewModel.mldClearAllCache.observe(this, Observer {
            if (it == null) return@Observer
            GlobalScope.launch(Dispatchers.IO) {
                delay(1000)
                viewModel.getCacheSize()
                if (it) getString(R.string.clear_cache_succeed).showToastOnThread()
                else getString(R.string.clear_cache_failed).showToastOnThread()
            }
            viewModel.mldClearAllCache.postValue(null)
        })
        viewModel.getCacheSize()
        mBinding.tvSettingActivityClearCache.isFocused = true
        mBinding.rlSettingActivityClearCache.setOnClickListener {
            MaterialDialog(this).show {
                icon(R.drawable.ic_sd_storage_main_color_2_24_skin)
                title(res = R.string.warning)
                message(text = "确定清理所有缓存？不包括缓存视频")
                positiveButton(res = R.string.clean) { viewModel.clearAllCache() }
                negativeButton(res = R.string.cancel) { dismiss() }
            }
        }

        mBinding.run {
            ivSettingActivityDownloadInfo.setOnClickListener {
                MaterialDialog(this@SettingActivity).show {
                    title(res = R.string.attention)
                    message(
                        text = "由于新版Android存储机制变更，因此新缓存的动漫将存储在App的私有路径，" +
                                "以前缓存的动漫依旧能够观看，其后面将有“旧”字样。新缓存的动漫与以前缓存的互不影响。" +
                                "\n\n注意：新缓存的动漫将在App被卸载或数据被清除后丢失。"
                    )
                    positiveButton { dismiss() }
                }
            }

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
                        tvSettingActivityUpdateTip.text = getString(R.string.checking_update)
                    }
                    AppUpdateStatus.DATED -> {
                        tvSettingActivityUpdateTip.text = getString(R.string.find_new_version)
                        if (selfUpdateCheck) appUpdateHelper.noticeUpdate(this@SettingActivity)
                    }
                    AppUpdateStatus.VALID -> {
                        tvSettingActivityUpdateTip.text = getString(R.string.is_latest_version)
                        if (selfUpdateCheck) getString(R.string.is_latest_version).showToast()
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
            builder.setIcon(R.drawable.ic_storage_main_color_2_24_skin)
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

        mBinding.tvSettingActivityInfoDomain.text = Api.MAIN_URL
//        mBinding.tvSettingActivityDefaultDomain.setOnClickListener {
//            Api.MAIN_URL = Api.DEFAULT_MAIN_URL
//            mBinding.tvSettingActivityInfoDomain.text = Api.DEFAULT_MAIN_URL
//            getString(R.string.set_domain_to_default, Api.DEFAULT_MAIN_URL)
//                .showToast(Toast.LENGTH_LONG)
//        }

        mBinding.switchSettingActivityCustomDataSource.isChecked =
            DataSourceManager.useCustomDataSource
        mBinding.switchSettingActivityCustomDataSource.setOnCheckedChangeListener { buttonView, isChecked ->
            if (DataSourceManager.useCustomDataSource == isChecked) return@setOnCheckedChangeListener
            MaterialDialog(this).show {
                icon(R.drawable.ic_category_main_color_2_24_skin)
                title(res = R.string.warning)
                message(res = R.string.request_restart_app)
                cancelable(false)
                positiveButton(res = R.string.restart) {
                    DataSourceManager.useCustomDataSource = isChecked
                    DataSourceManager.clearCache()
                    restartApp()
                }
                negativeButton(res = R.string.cancel) {
                    buttonView.isChecked = !isChecked
                    dismiss()
                }
            }
        }

//        mBinding.rlSettingActivityDomain.setOnClickListener {
//            MaterialDialog(this).show {
//                input(hintRes = R.string.input_a_website_domain) { dialog, text ->
//                    try {
//                        URL(text.toString())
//                        val url = text.toString().replaceFirst(Regex("/$"), "")
//                        Api.MAIN_URL = url
//                        mBinding.tvSettingActivityInfoDomain.text = url
//                    } catch (e: Exception) {
//                        App.context.resources.getString(R.string.website_domain_format_error)
//                            .showToast()
//                        e.printStackTrace()
//                    }
//                }
//                positiveButton(R.string.ok)
//            }
//        }

        initNightMode()
    }

    private fun initNightMode() {
        mBinding.run {
            when (SkinManager.getDarkMode()) {
                SkinManager.DARK_MODE_YES -> {
                    switchSettingActivityNightMode.isChecked = true
                    cbSettingActivityNightModeFollowSystem.isChecked = false
                    tvSettingActivityNightModeInfo.text = getString(R.string.dark)
                }
                SkinManager.DARK_MODE_NO -> {
                    switchSettingActivityNightMode.isChecked = false
                    cbSettingActivityNightModeFollowSystem.isChecked = false
                    tvSettingActivityNightModeInfo.text = getString(R.string.light)
                }
                SkinManager.DARK_FOLLOW_SYSTEM -> {
                    switchSettingActivityNightMode.isEnabled = false
                    cbSettingActivityNightModeFollowSystem.isChecked = true
                    tvSettingActivityNightModeInfo.text = getString(R.string.follow_system)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cbSettingActivityNightModeFollowSystem.isEnabled = true
                cbSettingActivityNightModeFollowSystem.setOnCheckedChangeListener { buttonView, isChecked ->
                    switchSettingActivityNightMode.isEnabled = !isChecked
                    if (isChecked) {
                        switchSettingActivityNightMode.isChecked = false
                        SkinManager.setDarkMode(SkinManager.DARK_FOLLOW_SYSTEM)
                        tvSettingActivityNightModeInfo.text = getString(R.string.follow_system)
                    } else {
                        SkinManager.setDarkMode(SkinManager.DARK_MODE_NO)
                        tvSettingActivityNightModeInfo.text = getString(R.string.light)
                    }
                }
            } else {
                cbSettingActivityNightModeFollowSystem.gone()
                cbSettingActivityNightModeFollowSystem.isEnabled = false
            }

            switchSettingActivityNightMode.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    SkinManager.setDarkMode(SkinManager.DARK_MODE_YES)
                    tvSettingActivityNightModeInfo.text = getString(R.string.dark)
                } else {
                    SkinManager.setDarkMode(SkinManager.DARK_MODE_NO)
                    tvSettingActivityNightModeInfo.text = getString(R.string.light)
                }
            }
        }
    }

    override fun getBinding(): ActivitySettingBinding =
        ActivitySettingBinding.inflate(layoutInflater)
}
