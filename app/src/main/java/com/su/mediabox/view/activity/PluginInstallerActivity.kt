package com.su.mediabox.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.su.mediabox.databinding.ActivityPluginInstallerBinding
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.util.*
import com.su.mediabox.viewmodel.PluginInstallerViewModel
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.submitList

class PluginInstallerActivity : BaseActivity(), View.OnClickListener {

    private val viewModel by viewModels<PluginInstallerViewModel>()
    private lateinit var mBinding: ActivityPluginInstallerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPluginInstallerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            viewModel.pluginInstallState.observe(this@PluginInstallerActivity) {
                pluginInstallLoading.gone()
                when (it) {
                    is PluginInstallerViewModel.PluginInstallState.LOADING -> {
                        pluginInstallDownload.gone()
                        pluginInstallInstall.gone()
                        pluginInstallLoading.visible()
                    }
                    is PluginInstallerViewModel.PluginInstallState.PREVIEW -> {
                        pluginInstallDownload.visible()
                        pluginInstallInstall.gone()
                        pluginInstallInfoList.submitList(it.previewInfo)
                    }
                    is PluginInstallerViewModel.PluginInstallState.READY -> {
                        pluginInstallDownload.gone()
                        pluginInstallInstall.visible()
                        pluginInstallInfoList.submitList(it.installInfo)
                    }
                    is PluginInstallerViewModel.PluginInstallState.SUCCESS -> {
                        pluginInstallDownload.gone()
                        pluginInstallInstall.gone()
                        pluginInstallLaunch.apply {
                            setOnClickListener { _ ->
                                this@PluginInstallerActivity.apply {
                                    launchPlugin(it.pluginInfo)
                                }
                                finish()
                            }
                            visible()
                        }
                    }
                    is PluginInstallerViewModel.PluginInstallState.ERROR -> {
                        pluginInstallDownload.gone()
                        pluginInstallInstall.gone()
                        pluginInstallInfoList.submitList(it.errorInfo)
                    }
                }
            }

            setViewsOnClickListener(
                pluginInstallInstall,
                pluginInstallCancel,
                pluginInstallDownload
            )

            pluginInstallInfoList.dynamicGrid().initTypeList { }
        }

        load(intent)
    }

    private fun load(intent: Intent?) {
        requestManageExternalStorage {
            onGranted {
                viewModel.load(intent)
            }
            onDenied {
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        load(intent)
    }

    override fun onClick(v: View?) {
        mBinding.apply {
            when (v) {
                pluginInstallInstall -> viewModel.install()
                pluginInstallCancel -> finish()
                pluginInstallDownload -> {
                    Toast.makeText(
                        this@PluginInstallerActivity,
                        "插件开始下载，请注意通知栏", Toast.LENGTH_LONG
                    ).show()
                    viewModel.downloadPlugin()
                    finish()
                }
            }
        }
    }

    //override fun onBackPressed() {}
}