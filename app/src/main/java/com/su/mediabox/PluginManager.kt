package com.su.mediabox

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.plugin.Constant
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.plugin.IComponentFactory
import com.su.mediabox.plugin.interfaces.IBase
import com.su.mediabox.plugin.interfaces.IRouteProcessor
import dalvik.system.DexClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object PluginManager : IRouteProcessor {

    const val PLUGIN_OPTI_FOLDER_NAME = "PluginsOpti"
    const val PLUGIN_INIT_CLASS = "com.su.mediabox.plugin.ComponentFactory"

    private val componentFactoryPool = mutableMapOf<String, IComponentFactory>()
    private val componentPool = mutableMapOf<String, MutableMap<Class<out IBase>, IBase>>()

    /**
     * 最低支持的插件API版本
     */
    private const val minPluginApiVersion = 1

    private val _pluginLiveData = MutableLiveData<List<PluginInfo>>()
    private val pluginIntent = Intent(Constant.PLUGIN_ACTION)
    private val pluginWorkScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val pluginLiveData: LiveData<List<PluginInfo>>
        get() = _pluginLiveData

    fun scanPlugin(packageManager: PackageManager) {
        pluginWorkScope.launch {
            val plugin = packageManager.queryIntentActivities(pluginIntent, 0).map {
                PluginInfo(
                    it.activityInfo.packageName,
                    it.activityInfo.name,
                    it.loadLabel(packageManager).toString(),
                    it.loadIcon(packageManager),
                    it.activityInfo.applicationInfo.sourceDir
                )
            }
            _pluginLiveData.postValue(plugin)
        }
    }

    fun Activity.getPluginIndex() = intent.getIntExtra(BasePluginActivity.PLUGIN_INFO_INDEX, -1)

    fun Activity.getPluginInfo(): PluginInfo {
        runCatching {
            val index = getPluginIndex()
            if (index == -1)
                throw RuntimeException()
            _pluginLiveData.value?.get(index) ?: throw RuntimeException()
        }.onSuccess {
            return it
        }
        throw RuntimeException("插件信息读取错误")
    }

    fun Activity.getPluginName() = getPluginInfo().name
    fun Activity.getPluginPath() = getPluginInfo().sourcePath

    fun Intent.setPluginInfo(pluginInfoIndex: Int) {
        putExtra(BasePluginActivity.PLUGIN_INFO_INDEX, pluginInfoIndex)
    }

    fun acquireComponentFactory() =
        AppRouteProcessor.currentActivity?.get()?.getPluginPath()
            ?.let { acquireComponentFactory(it) }
            ?: throw RuntimeException("当前未绑定插件")

    private val processor by lazy(LazyThreadSafetyMode.NONE) { acquireComponent(IRouteProcessor::class.java) }

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

            val optimizedDirectory =
                File(App.context.getExternalFilesDir(null).toString() + "/$PLUGIN_OPTI_FOLDER_NAME")
                    .apply {
                        if (!exists() && !mkdirs())
                            throw RuntimeException("创建插件优化文件夹失败")
                    }

            val classLoader = DexClassLoader(
                pluginFile.path, optimizedDirectory.path,
                null, App.context.classLoader
            )
            val clz = classLoader.loadClass(PLUGIN_INIT_CLASS)

            //检查插件API版本
            val version = clz.getAnnotation(IComponentFactory.PluginSdkVersion::class.java)
                ?: throw RuntimeException("插件初始化错误")
            if (version.version < minPluginApiVersion)
                throw RuntimeException("该插件API版本过低，请联系作者升级API")

            (clz.newInstance() as IComponentFactory).also {
                componentFactoryPool[pluginPath] = it
            }
        }

    @Throws(Exception::class)
    fun <T : IBase> acquireComponent(clazz: Class<T>) =
        AppRouteProcessor.currentActivity?.get()?.getPluginPath()
            ?.let {
                acquireComponent(it, clazz)
            } ?: throw RuntimeException("当前未绑定插件")

    /**
     * 获取组件实例
     */
    @Throws(Exception::class)
    fun <T : IBase> acquireComponent(pluginPath: String, clazz: Class<T>): T {
        val isSingleton =
            clazz.isAnnotationPresent(IComponentFactory.SingletonComponent::class.java)
        if (isSingleton) {
            //被标注为单例组件，从组件池查找
            componentPool[pluginPath]?.get(clazz)?.also { return it as T }
        }
        return acquireComponentFactory(pluginPath).create(clazz)?.also { component ->
            if (isSingleton)
            //存入组件库
                (componentPool[pluginPath] ?: mutableMapOf<Class<out IBase>, IBase>()
                    .also {
                        componentPool[pluginPath] = it
                    })[clazz] = component
        }
            ?: throw RuntimeException("当前插件未提供该组件")
    }

    override fun process(actionUrl: String): Boolean =
        processor.process(actionUrl).let { if (it) it else AppRouteProcessor.process(actionUrl) }

}