package com.su.mediabox.viewmodel

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import com.su.mediabox.util.logD
import android.view.Gravity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.net.RetrofitManager
import com.su.mediabox.net.service.PluginService
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.TextUtil.urlDecode
import com.su.mediabox.util.FileUri
import com.su.mediabox.util.Util
import com.su.mediabox.util.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PluginInstallerViewModel : ViewModel() {

    private val _pluginInstallState =
        MutableLiveData<PluginInstallState>(PluginInstallState.LOADING)
    val pluginInstallState = _pluginInstallState.toLiveData()

    fun load(intent: Intent?) {
        intent?.data?.apply {
            logD("插件安装载入", intent.toString())
            when (scheme) {
                "file", "content" -> localLoad(this)
                "mediabox" -> onlineLoad(this)
                else -> _pluginInstallState.value =
                    PluginInstallState.ERROR(buildInfoPair("Uri错误", "请尝试更换文件管理器打开", Color.RED))
            }
        }
    }

    fun install() {
        viewModelScope.launch {
            when (val data = pluginInstallState.value) {
                is PluginInstallState.READY -> {
                    PluginManager.installPlugin(data.fileUrl, data.pluginInfo).apply {
                        _pluginInstallState.postValue(
                            if (exists())
                                PluginInstallState.SUCCESS(data.pluginInfo)
                            else
                                PluginInstallState.ERROR(buildInfoPair("安装错误", "请检查读写权限"))
                        )
                    }
                }
            }
        }
    }

    private fun localLoad(data: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUri.getPathByUri(data)?.also { url ->
                var path = url
                repeat(3) {
                    path = path.urlDecode()
                }
                logD("路径", path)
                //path.showToast(Toast.LENGTH_LONG)

                val info = mutableListOf<SimpleTextData>()

                PluginManager.parsePluginInfo(path)?.apply {
                    info.addAll(buildInfoPair("来源", sourcePath))
                    info.addAll(buildInfoPair("名称", name))
                    info.addAll(buildInfoPair("包名", packageName))
                    info.addAll(buildInfoPair("API", "$apiVersion"))
                    PluginManager.getPluginInfo(packageName)?.also {
                        //已安装检测
                        info.addAll(buildInfoPair("版本", "升级 ${it.version} -> $version", Color.RED))
                        //安全检测
                        if (signature != it.signature) {
                            info.addAll(
                                buildInfoPair("警告", "当前插件包与已安装插件签名不一致，请确认来源是否安全", Color.RED)
                            )
                            _pluginInstallState.postValue(
                                PluginInstallState.ERROR(info)
                            )
                            return@launch
                        }
                    } ?: info.addAll(buildInfoPair("版本", version))
                    _pluginInstallState.postValue(PluginInstallState.READY(data, this, info))
                } ?: run {
                    _pluginInstallState.postValue(
                        PluginInstallState.ERROR(
                            buildInfoPair("错误", "该文件不是插件包", Color.RED)
                        )
                    )
                }
            }
        }
    }

    private fun onlineLoad(data: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO 载入在线安装预览信息
            when (data.path) {
                "/install" -> try {
                    logD("在线安装", "$data")
                    val pluginInfoUrl =
                        Util.withoutExceptionGet {
                            data.getQueryParameter("previewInfoUrl")?.urlDecode()
                        }

                    if (pluginInfoUrl != null) {
                        logD("在线安装", "预览信息=$pluginInfoUrl")
                        val api = RetrofitManager.get().create(PluginService::class.java)
                        api.fetchPluginPreviewInfo(pluginInfoUrl)?.apply {
                            val info = mutableListOf<SimpleTextData>()

                            info.addAll(buildInfoPair("来源", pluginInfoUrl))
                            info.addAll(buildInfoPair("下载", sourcePath))
                            info.addAll(buildInfoPair("名称", name))
                            info.addAll(buildInfoPair("包名", packageName))
                            info.addAll(buildInfoPair("API", "$apiVersion"))

                            _pluginInstallState.postValue(
                                PluginInstallState.PREVIEW(this, info)
                            )

                            logD("预览信息", toString())
                            return@launch
                        }
                    }
                    PluginInstallState.ERROR(
                        buildInfoPair("错误", "加载预览信息失败", Color.RED)
                    )
                } catch (e: Exception) {
                    PluginInstallState.ERROR(
                        buildInfoPair("错误", "在线安装预览信息加载错误", Color.RED)
                    )
                }
            }
        }
    }

    fun downloadPlugin() {
        when (val data = pluginInstallState.value) {
            is PluginInstallState.PREVIEW -> {
                PluginManager.downloadPlugin(data.pluginInfo)
            }
        }
    }

    private fun buildInfoPair(type: String, data: String, color: Int = Color.BLACK) =
        listOf(SimpleTextData(type).apply {
            spanSize = 2
            fontColor = color
            fontStyle = Typeface.BOLD
        }, SimpleTextData(data).apply {
            spanSize = 6
            gravity = Gravity.RIGHT
            fontColor = color
        })


    sealed class PluginInstallState {
        object LOADING : PluginInstallState()

        //准备就绪
        class READY(val fileUrl: Uri, val pluginInfo: PluginInfo, val installInfo: List<BaseData>) :
            PluginInstallState()

        class SUCCESS(val pluginInfo: PluginInfo) : PluginInstallState()

        class ERROR(val errorInfo: List<BaseData>) : PluginInstallState()

        //在线安装时的预览插件包信息
        class PREVIEW(val pluginInfo: PluginInfo, val previewInfo: List<BaseData>) :
            PluginInstallState()
    }
}