package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivitySettingBinding
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.net.DnsServer.selectDnsServer
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.viewmodel.SettingViewModel
import com.skyd.skin.SkinManager
import kotlinx.coroutines.*


class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private val viewModel: SettingViewModel by lazy { ViewModelProvider(this).get(SettingViewModel::class.java) }
    private var selfUpdateCheck = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appUpdateHelper = AppUpdateHelper.instance

        mBinding.run {
            atbSettingActivityToolbar.setBackButtonClickListener { finish() }

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
                icon(drawable = getResDrawable(R.drawable.ic_delete_main_color_2_24_skin))
                title(res = R.string.warning)
                message(text = "确定要删除所有历史记录？包括搜索历史和观看历史")
                positiveButton(res = R.string.delete) { viewModel.deleteAllHistory() }
                negativeButton(res = R.string.cancel) { dismiss() }
            }
        }

        // 清理缓存文件
        viewModel.mldCacheSize.observe(this, {
            mBinding.tvSettingActivityClearCacheSize.text = it
        })
        viewModel.mldClearAllCache.observe(this, Observer {
            if (it == null) return@Observer
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)
                viewModel.getCacheSize()
                if (it) getString(R.string.clear_cache_succeed).showToast()
                else getString(R.string.clear_cache_failed).showToast()
            }
            viewModel.mldClearAllCache.postValue(null)
        })
        viewModel.getCacheSize()
        mBinding.tvSettingActivityClearCache.isFocused = true
        mBinding.rlSettingActivityClearCache.setOnClickListener {
            MaterialDialog(this).show {
                icon(drawable = getResDrawable(R.drawable.ic_sd_storage_main_color_2_24_skin))
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
                AppUpdateStatus.CHECKING -> {
                    "已在检查，请稍等...".showToast()
                }
                else -> appUpdateHelper.checkUpdate()
            }
        }

        mBinding.tvSettingActivityInfoDomain.text = Api.MAIN_URL

        mBinding.tvSettingActivityCustomDataSource.text =
            getString(R.string.custom_data_source, DataSourceManager.dataSourceName.let {
                if (it == DataSourceManager.DEFAULT_DATA_SOURCE)
                    getString(R.string.default_data_source)
                else it
            })

        mBinding.rlSettingActivityCustomDataSource.setOnClickListener {
            startActivity(Intent(this, ConfigDataSourceActivity::class.java))
        }

        mBinding.rlSettingActivityDoh.setOnClickListener { selectDnsServer() }

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
