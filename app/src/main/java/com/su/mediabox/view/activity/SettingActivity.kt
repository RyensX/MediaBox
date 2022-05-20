package com.su.mediabox.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivitySettingBinding
import com.su.mediabox.net.DnsServer.selectDnsServer
import com.su.mediabox.util.Util.getAppVersionName
import com.su.mediabox.util.Util.getResDrawable
import com.su.mediabox.util.gone
import com.su.mediabox.util.showToast
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.util.update.AppUpdateStatus
import com.su.mediabox.util.viewBind
import com.su.mediabox.util.visible
import com.su.mediabox.viewmodel.SettingViewModel
import kotlinx.coroutines.*

@Deprecated("需要重新设计")
class SettingActivity : BaseActivity() {

    private val mBinding by viewBind(ActivitySettingBinding::inflate)
    private val viewModel: SettingViewModel by lazy(LazyThreadSafetyMode.NONE) { ViewModelProvider(this).get(SettingViewModel::class.java) }
    private var selfUpdateCheck = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //UP_TODO 2022/2/14 15:10 0 暂时移除检查更新
        val appUpdateHelper = AppUpdateHelper.instance

        mBinding.run {
            atbSettingActivityToolbar.setBackButtonClickListener { finish() }

            tvSettingActivityDownloadPathInfo.isFocused = true
            tvSettingActivityDownloadPathInfo.text = Const.DownloadAnime.animeFilePath
        }

        //UP_TODO 2022/2/19 12:32 0 暂时移除，因为后面每个插件会有独立数据管理
        /**
        viewModel.getAllHistoryCount()
        viewModel.mldAllHistoryCount.observe(this) {
            if (it >= 0) {
                mBinding.tvSettingActivityAllHistoryCount.apply {
                    visible()
                    text = getString(R.string.all_history_count, it)
                }
            } else mBinding.tvSettingActivityAllHistoryCount.gone()
        }
        // 清理历史记录
        viewModel.mldDeleteAllHistory.observe(this, Observer {
            if (it == null) return@Observer
            if (it) getString(R.string.delete_all_history_succeed).showToast()
            else getString(R.string.delete_all_history_failed).showToast()
            viewModel.mldDeleteAllHistory.postValue(null)
        })
        mBinding.rlSettingActivityDeleteAllHistory.setOnClickListener {
            MaterialDialog(this).show {
                icon(drawable = getResDrawable(R.drawable.ic_delete_main_color_2_24_skin))
                title(res = R.string.warning)
                message(res = R.string.confirm_delete_all_history)
                positiveButton(res = R.string.delete) { viewModel.deleteAllHistory() }
                negativeButton(res = R.string.cancel) { dismiss() }
            }
        }
        */

        // 清理缓存文件
        viewModel.mldCacheSize.observe(this) {
            mBinding.tvSettingActivityClearCacheSize.text = it
        }
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
                        text = "注意：缓存的视频将在App被卸载或数据被清除后丢失。"
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

        mBinding.rlSettingActivityDoh.setOnClickListener { selectDnsServer() }

    }

}
