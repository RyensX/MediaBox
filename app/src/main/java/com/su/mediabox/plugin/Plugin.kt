package com.su.mediabox.plugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.pluginapi.IComponentFactory
import com.su.mediabox.pluginapi.components.IBaseComponent
import com.su.mediabox.util.Util.getSignatures
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.toLiveData
import com.su.mediabox.v2.view.activity.HomeActivity
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

object PluginManager {

    private val componentFactoryPool = mutableMapOf<String, IComponentFactory>()
    private val componentPool =
        mutableMapOf<String, MutableMap<Class<out IBaseComponent>, IBaseComponent>>()

    /**
     * 最低支持的插件API版本
     */
    private const val minPluginApiVersion = 4

    /**
     * Map<[BasePluginActivity.PLUGIN_ID],[PluginInfo]>
     */
    private val pluginDataFlow = MutableStateFlow(mutableMapOf<String, PluginInfo>())
    private val _currentLaunchPlugin = MutableLiveData<PluginInfo?>()
    private val pluginIntent = Intent(Constant.PLUGIN_ACTION)
    private val pluginWorkScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val pluginLiveData: LiveData<List<PluginInfo>> = pluginDataFlow
        .map {
            it.values.toList()
        }
        .flowOn(Dispatchers.Default)
        .asLiveData()

    val currentLaunchPlugin = _currentLaunchPlugin.toLiveData()

    fun scanPlugin(packageManager: PackageManager) {
        pluginWorkScope.launch {
            val plugins = mutableMapOf<String, PluginInfo>()
            packageManager.queryIntentActivities(pluginIntent, 0).forEach { info ->
                PluginInfo(
                    info.activityInfo.applicationInfo.metaData?.getInt("api_version", -1) ?: -1,
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    info.activityInfo.applicationInfo.loadLabel(packageManager).toString(),
                    info.loadIcon(packageManager),
                    info.activityInfo.applicationInfo.sourceDir,
                    packageManager.getSignatures(info.activityInfo.packageName)
                ).also {
                    plugins[it.packageName] = it
                }
            }
            pluginDataFlow.value = plugins
        }
    }

    fun Context.launchPlugin(pluginInfo: PluginInfo?) {
        pluginInfo?.apply {
            _currentLaunchPlugin.value = this
            goActivity<HomeActivity>()
        }
    }

    fun initPluginEnv() {
        _currentLaunchPlugin.value = null
    }

    /**
     * 获取组件工厂实例
     */
    @Throws(Exception::class)
    fun acquireComponentFactory(pluginPath: String): IComponentFactory =
        componentFactoryPool[pluginPath] ?: run {
            val pluginFile = File(pluginPath)
                .apply {
                    if (!exists() || !isFile)
                        throw RuntimeException("插件不存在")
                }

            val classLoader = PathClassLoader(pluginFile.path, App.context.classLoader)
            val clz = classLoader.loadClass(Constant.PLUGIN_INIT_CLASS)

            (clz.newInstance() as IComponentFactory).also {
                componentFactoryPool[pluginPath] = it
            }
        }

    @Throws(Exception::class)
    inline fun <reified T : IBaseComponent> PluginInfo.acquireComponent() =
        acquireComponent(sourcePath, T::class.java)

    @Throws(Exception::class)
    inline fun <reified T : IBaseComponent> acquireComponent() =
        currentLaunchPlugin.value?.acquireComponent<T>() ?: throw RuntimeException("当前未启动插件！")

    /**
     * 获取组件实例
     */
    @Throws(Exception::class)
    fun <T : IBaseComponent> acquireComponent(pluginPath: String, clazz: Class<T>): T {
        val isSingleton =
            clazz.isAnnotationPresent(IComponentFactory.SingletonComponent::class.java)
        if (isSingleton) {
            //被标注为单例组件，从组件池查找
            componentPool[pluginPath]?.get(clazz)?.also { return it as T }
        }
        return acquireComponentFactory(pluginPath).createComponent(clazz)?.also { component ->
            if (isSingleton)
            //存入组件库
                (componentPool[pluginPath]
                    ?: mutableMapOf<Class<out IBaseComponent>, IBaseComponent>()
                        .also {
                            componentPool[pluginPath] = it
                        })[clazz] = component
        }
            ?: throw RuntimeException("当前插件未提供该组件")
    }

}