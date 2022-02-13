package com.skyd.imomoe

import android.app.Activity
import com.skyd.imomoe.view.activity.BasePluginActivity
import com.su.mediabox.plugin.IComponentFactory
import com.su.mediabox.plugin.interfaces.IBase
import com.su.mediabox.plugin.interfaces.IRouteProcessor
import dalvik.system.DexClassLoader
import java.io.File

object PluginManager : IRouteProcessor {

    const val PLUGIN_FOLDER_NAME = "Plugins"
    const val PLUGIN_OPTI_FOLDER_NAME = "PluginsOpti"
    const val PLUGIN_INIT_CLASS = "com.su.mediabox.plugin.ComponentFactory"

    private val componentFactoryPool = mutableMapOf<String, IComponentFactory>()
    private val componentPool = mutableMapOf<String, MutableMap<Class<out IBase>, IBase>>()

    /**
     * 最低支持的插件API版本
     */
    private const val minPluginApiVersion = 1

    fun Activity.getPluginName() = intent.getStringExtra(BasePluginActivity.PLUGIN_NAME)

    fun acquireComponentFactory() =
        AppRouteProcessor.currentActivity?.get()?.getPluginName()
            ?.let { acquireComponentFactory(it) }
            ?: throw RuntimeException("当前未绑定插件")

    private val processor by lazy(LazyThreadSafetyMode.NONE) { acquireComponent(IRouteProcessor::class.java) }

    /**
     * 获取组件工厂实例
     */
    @Throws(Exception::class)
    fun acquireComponentFactory(pluginName: String): IComponentFactory =
        componentFactoryPool[pluginName] ?: run {
            val pluginFile = File(getPluginPath(pluginName))
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
                componentFactoryPool[pluginName] = it
            }
        }

    @Throws(Exception::class)
    fun <T : IBase> acquireComponent(clazz: Class<T>) =
        AppRouteProcessor.currentActivity?.get()?.getPluginName()
            ?.let {
                acquireComponent(it, clazz)
            } ?: throw RuntimeException("当前未绑定插件")

    /**
     * 获取组件实例
     */
    @Throws(Exception::class)
    fun <T : IBase> acquireComponent(pluginName: String, clazz: Class<T>): T {
        val isSingleton =
            clazz.isAnnotationPresent(IComponentFactory.SingletonComponent::class.java)
        if (isSingleton) {
            //被标注为单例组件，从组件池查找
            componentPool[pluginName]?.get(clazz)?.also { return it as T }
        }
        return acquireComponentFactory(pluginName).create(clazz)?.also { component ->
            if (isSingleton)
            //存入组件库
                (componentPool[pluginName] ?: mutableMapOf<Class<out IBase>, IBase>()
                    .also {
                        componentPool[pluginName] = it
                    })[clazz] = component
        }
            ?: throw RuntimeException("当前插件未提供该组件")
    }

    fun getPluginPath(pluginName: String): String =
        "${getPluginsDirectory()}/${pluginName}"

    fun getPluginsDirectory(): String {
        return "${App.context.getExternalFilesDir(null).toString()}/$PLUGIN_FOLDER_NAME"
    }

    override fun process(actionUrl: String): Boolean =
        processor.process(actionUrl).let { if (it) it else AppRouteProcessor.process(actionUrl) }

}