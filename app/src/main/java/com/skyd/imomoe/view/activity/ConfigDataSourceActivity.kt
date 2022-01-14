package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityConfigDataSourceBinding
import com.skyd.imomoe.view.adapter.ConfigDataSourceAdapter
import com.skyd.imomoe.viewmodel.ConfigDataSourceViewModel
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.*
import java.io.File


class ConfigDataSourceActivity : BaseActivity<ActivityConfigDataSourceBinding>() {
    private lateinit var viewModel: ConfigDataSourceViewModel
    private lateinit var adapter: ConfigDataSourceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ConfigDataSourceViewModel::class.java)
        adapter = ConfigDataSourceAdapter(this, viewModel.dataSourceList)

        callToImport(intent)
        mBinding.apply {
            rvDataSourceConfigActivity.layoutManager =
                LinearLayoutManager(this@ConfigDataSourceActivity)
            rvDataSourceConfigActivity.adapter = adapter
            atbDataSourceConfigActivity.setBackButtonClickListener { finish() }
            atbDataSourceConfigActivity.setButtonClickListener(0) { resetDataSource() }
        }

        viewModel.mldDataSourceList.observe(this, {
            adapter.smartNotifyDataSetChanged(it.first, it.second, viewModel.dataSourceList)
        })

        viewModel.getDataSourceList()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { callToImport(it) }
    }

    private fun callToImport(intent: Intent) {
        val uri = intent.data
        if (Intent.ACTION_VIEW == intent.action && uri != null) {
            requestManageExternalStorage {
                onGranted {
                    importDataSource(uri,
                        onSuccess = {
                            getString(
                                R.string.import_data_source_success,
                                uri.path
                            ).showSnackbar(this@ConfigDataSourceActivity)
                            viewModel.getDataSourceList()
                        },
                        onFailed = {
                            getString(
                                R.string.import_data_source_failed,
                                it.message
                            ).showSnackbar(this@ConfigDataSourceActivity)
                        }
                    )
                }
                onDenied {
                    "无存储权限，无法导入".showSnackbar(this@ConfigDataSourceActivity, Toast.LENGTH_LONG)
                }
            }
        }
    }

    fun resetDataSource(runBeforeReset: (() -> Unit)? = null) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.request_restart_app)
            positiveButton(res = R.string.restart) {
                runBeforeReset?.invoke()
                viewModel.resetDataSource()
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    fun setDataSource(name: String, showDialog: Boolean = true) {
        if (!showDialog) {
            viewModel.setDataSource(name)
            return
        }
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.custom_data_source_tip)
            cancelable(false)
            positiveButton(res = R.string.restart) {
                viewModel.setDataSource(name)
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    fun deleteDataSource(bean: DataSourceFileBean) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.ask_delete_data_source)
            positiveButton(res = R.string.ok) {
                if (DataSourceManager.dataSourceName == bean.file.name) {
                    resetDataSource { viewModel.deleteDataSource(bean) }
                } else {
                    viewModel.deleteDataSource(bean)
                }
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    fun askOverwriteFile(needRestartApp: Boolean = false, callback: (Boolean) -> Unit) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.ask_overwrite_file)
            cancelable(false)
            positiveButton(
                res = if (needRestartApp) R.string.overwrite_file_and_restart
                else R.string.overwrite_file
            ) { callback.invoke(true) }
            negativeButton(res = R.string.do_not_overwrite_file) { callback.invoke(false) }
        }
    }

    fun importDataSource(
        uri: Uri,
        onSuccess: ((File) -> Unit)? = null,
        onFailed: ((Exception) -> Unit)? = null
    ) {
        val dataSourceSuffix = (uri.path ?: "").substringAfterLast(".", "")
        if (!dataSourceSuffix.equals("ads", true)) {
            getString(R.string.invalid_data_source_suffix, dataSourceSuffix)
                .showSnackbar(this, duration = Toast.LENGTH_LONG)
            return
        }
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.import_data_source)
            cancelable(false)
            positiveButton(res = R.string.ok) {
                try {
                    val sourceFileName = uri.getPathFromURI(this@ConfigDataSourceActivity)!!
                        .substringAfterLast("/")
                    val directory = File(DataSourceManager.getJarDirectory())
                    if (!directory.exists()) directory.mkdirs()
                    val target = File(
                        DataSourceManager.getJarDirectory() + "/" + sourceFileName
                    )
                    if (target.exists()) {
                        val needRestartApp = DataSourceManager.dataSourceName == sourceFileName
                        askOverwriteFile(needRestartApp) {
                            if (!it) onFailed?.invoke(
                                FileAlreadyExistsException(
                                    file = target,
                                    reason = "file already exists"
                                )
                            )
                            else {
                                Thread {
                                    if (needRestartApp) Util.restartApp()
                                    else {
                                        uri.copyTo(target)
                                        runOnUiThread { onSuccess?.invoke(target) }
                                    }
                                }.start()
                            }
                        }
                    } else {
                        target.createNewFile()
                        Thread {
                            uri.copyTo(target)
                            runOnUiThread { onSuccess?.invoke(target) }
                        }.start()
                    }
                } catch (e: Exception) {
                    onFailed?.invoke(e)
                }
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    override fun getBinding(): ActivityConfigDataSourceBinding =
        ActivityConfigDataSourceBinding.inflate(layoutInflater)
}