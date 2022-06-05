package com.su.mediabox.plugin

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.FileObserver
import android.text.Html
import com.su.mediabox.util.logD
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.database.destroyInstance
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.database.getAppDataBaseFileName
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.IPluginFactory
import com.su.mediabox.pluginapi.components.IBasePageDataComponent
import com.su.mediabox.util.*
import com.su.mediabox.util.Text.githubProxy
import com.su.mediabox.util.Util.getSignatures
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object PluginManager {

    private val pluginFactoryPool = mutableMapOf<String, IPluginFactory>()
    private val componentPool =
        mutableMapOf<String, MutableMap<Class<out IBasePageDataComponent>, IBasePageDataComponent>>()

    /**
     * 最低支持的插件API版本
     */
    private const val minPluginApiVersion = 1

    const val PLUGIN_DIR_NAME = "plugins"
    val pluginDir = App.context.getExternalFilesDir(PLUGIN_DIR_NAME)!!

    init {
        PluginPackageObserver.startWatching()
    }

    /**
     * Map<packageName,[PluginInfo]>
     */
    private val pluginDataFlow = MutableStateFlow(mutableMapOf<String, PluginInfo>())
    private val _currentLaunchPlugin = MutableLiveData<PluginInfo?>()
    private val pluginIntent = Intent(Constant.PLUGIN_DEBUG_ACTION)
    private val pluginWorkScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val pluginFlow: Flow<List<PluginInfo>> = pluginDataFlow
        .map {
            it.values.toList()
        }
        .flowOn(Dispatchers.Default)

    val pluginLiveData: LiveData<List<PluginInfo>> = pluginFlow.asLiveData()

    val currentLaunchPlugin = _currentLaunchPlugin.toLiveData().apply {
        observeForever {
            //每次退出插件
        }
    }

    fun queryPluginInfo(packageName: String) = pluginDataFlow.value[packageName]

    fun scanPlugin() {
        val packageManager = App.context.packageManager
        pluginWorkScope.launch {
            val plugins = mutableMapOf<String, PluginInfo>()
            //内部安装的插件，如果有外部相同包名的则会被覆盖以方便调试
            pluginDir.listFiles()?.apply {
                logD("内部插件数量", "$size")
            }?.forEach { pluginPackage ->
                val path = pluginPackage.absolutePath
                logD("内部插件", path)
                parsePluginInfo(path)?.also { plugins[it.packageName] = it }
            }
            //扫描已安装的，只在debug模式下有效以方便调试
            debug {
                packageManager.queryIntentActivities(pluginIntent, 0).apply {
                    logD("外部插件数量", "$size")
                }.forEach { info ->
                    val path = info.activityInfo.applicationInfo.sourceDir
                    logD("外部插件", path)
                    parsePluginInfo(path)?.also {
                        it.isExternalPlugin = true
                        plugins[it.packageName] = it
                    }
                }
            }
            logD("插件扫描完毕", "数量:${plugins.size}")
            pluginDataFlow.value = plugins
        }
    }

    fun getPluginInfo(packageName: String) = pluginDataFlow.value[packageName]

    fun parsePluginInfo(pluginPackagePath: String): PluginInfo? {
        App.context.packageManager.getPackageArchiveInfo(
            pluginPackagePath,
            PackageManager.GET_META_DATA or PackageManager.GET_SIGNATURES
        )?.apply {
            //部分系统（尤其是低版本）上存在加载资源失败的bug
            applicationInfo.apply {
                if (publicSourceDir.isNullOrEmpty())
                    publicSourceDir = pluginPackagePath
                if (sourceDir.isNullOrEmpty())
                    sourceDir = pluginPackagePath
            }
            return parsePluginInfo(this)
        }
        return null
    }

    fun parsePluginInfo(pluginPackageInfo: PackageInfo): PluginInfo? {
        val pluginApplicationInfo = pluginPackageInfo.applicationInfo
        val packageManager = App.context.packageManager
        //插件API版本标记
        val apiVersion =
            pluginApplicationInfo.metaData?.getInt("media_plugin_api_version", -1) ?: -1
        if (apiVersion == -1)
            return null
        //插件实现的组件工厂类(IComponentFactory)完整包名
        val apiImpl =
            pluginApplicationInfo.metaData?.getString("media_plugin_api_impl") ?: return null

        return PluginInfo(
            apiVersion,
            apiImpl,
            pluginApplicationInfo.packageName,
            pluginApplicationInfo.loadLabel(packageManager).toString(),
            pluginPackageInfo.versionName,
            pluginApplicationInfo.loadIcon(packageManager),
            pluginApplicationInfo.sourceDir,
            getSignatures(pluginPackageInfo)
        )
    }

    fun Context.launchPlugin(pluginInfo: PluginInfo?, isLaunchInitAction: Boolean = true) {
        pluginInfo?.apply {
            _currentLaunchPlugin.value = this
            if (isLaunchInitAction)
                acquirePluginFactory().initAction.go(this@launchPlugin)
        }
    }

    /**
     * 安装插件
     *
     * @param pluginInfo 至少要保证包含有效[PluginInfo.id]
     */
    suspend fun installPlugin(
        fileUri: Uri,
        pluginInfo: PluginInfo
    ): File =
        withContext(Dispatchers.IO) {
            logD("安装插件", "uri=$fileUri info=$pluginInfo")
            return@withContext fileUri.copyTo(File(pluginDir, pluginInfo.installedPluginName()))
                .apply {
                    App.context.getString(R.string.plugin_installer_hint_format, pluginInfo.name)
                        .showToast(Toast.LENGTH_LONG)
                }
        }

    /**
     * @param confirmContext 如果提供则有确认
     */
    fun uninstallPlugin(
        pluginInfo: PluginInfo,
        confirmContext: Context? = null,
        onComplete: (() -> Unit)? = null
    ) {
        if (confirmContext != null)
            MaterialDialog(confirmContext).show {
                title(res = R.string.plugin_manage_media_uninstall_title)
                message(res = R.string.plugin_manage_media_uninstall_msg)
                negativeButton(res = R.string.cancel) { }
                positiveButton(res = R.string.ok) {
                    uninstallPlugin(pluginInfo, onComplete = onComplete)
                }
            } else {
            pluginWorkScope.launch(Dispatchers.IO) {
                //删除数据
                val dbFile = App.context.getDatabasePath(pluginInfo.getAppDataBaseFileName())
                logD("数据库", dbFile.absolutePath)
                dbFile.parentFile?.listFiles()?.forEach {
                    if (it.name.startsWith(dbFile.name))
                        it.delete()
                }
                pluginInfo.destroyInstance()
                //删除插件包
                pluginDir.listFiles()?.forEach {
                    if (it.name.contains(pluginInfo.packageName)) {
                        it.delete()
                        return@launch
                    }
                }
            }
            onComplete?.invoke()
        }
    }

    /**
     * 调用系统下载器下载插件
     *
     * @param pluginInfo 至少要保证包含有效[PluginInfo.sourcePath]（作为下载地址）
     * @param directInstall 直接下载安装，一般只用于官方仓库插件，不经安装器验证直接安装
     */
    fun downloadPlugin(pluginInfo: PluginInfo, directInstall: Boolean = false) {
        val downloadManager =
            App.context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri
            .parse(pluginInfo.sourcePath.githubProxy)
        val request = DownloadManager.Request(uri).apply {
            val fileName = "${pluginInfo.name}_${pluginInfo.packageName}_${pluginInfo.version}.mpp"
            if (directInstall) {
                setDestinationInExternalFilesDir(App.context, PLUGIN_DIR_NAME, "tmp_$fileName")
            } else {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        downloadManager.enqueue(request)
    }

    fun initPluginEnv() {
        _currentLaunchPlugin.value = null
    }

    /**
     * 获取组件工厂实例
     */
    @Throws(Exception::class)
    fun PluginInfo.acquirePluginFactory(): IPluginFactory =
        pluginFactoryPool[sourcePath] ?: run {

            //判定API版本
            if (apiVersion < minPluginApiVersion)
                throw RuntimeException("插件API版本过低，请联系插件作者升级")

            val pluginFile = File(sourcePath)
                .apply {
                    if (!exists() || !isFile)
                        throw RuntimeException("插件不存在")
                }

            val classLoader = PathClassLoader(pluginFile.path, App.context.classLoader)

            try {
                val clz = classLoader.loadClass(apiImpl)
                (clz.newInstance() as IPluginFactory).also {
                    pluginFactoryPool[sourcePath] = it
                }
            } catch (e: Exception) {
                throw RuntimeException("插件工厂载入错误，请联系插件作者检查元信息")
            }
        }

    fun acquirePluginFactory(): IPluginFactory =
        currentLaunchPlugin.value?.acquirePluginFactory() ?: throw RuntimeException("当前未启动插件！")

    @Throws(Exception::class)
    inline fun <reified T : IBasePageDataComponent> acquireComponent() =
        acquireComponent(T::class.java)

    @Throws(Exception::class)
    fun <T : IBasePageDataComponent> acquireComponent(clazz: Class<T>) =
        currentLaunchPlugin.value?.acquireComponent(clazz)
            ?: throw RuntimeException("当前未启动插件！")

    /**
     * 获取组件实例
     */
    @Throws(Exception::class)
    fun <T : IBasePageDataComponent> PluginInfo.acquireComponent(clazz: Class<T>): T {
        val isSingleton =
            clazz.isAnnotationPresent(IPluginFactory.SingletonComponent::class.java)
        if (isSingleton) {
            //被标注为单例组件，从组件池查找
            componentPool[sourcePath]?.get(clazz)?.also { return it as T }
        }
        return acquirePluginFactory().createComponent(clazz)?.also { component ->
            if (isSingleton)
            //存入组件库
                (componentPool[sourcePath]
                    ?: mutableMapOf<Class<out IBasePageDataComponent>, IBasePageDataComponent>()
                        .also {
                            componentPool[sourcePath] = it
                        })[clazz] = component
        }
            ?: throw RuntimeException("当前插件未提供该组件")
    }

    private fun PluginInfo.installedPluginName() = "mediabox_plugin_${id}.mpp"

    /**
     * 插件包(安装/卸载)监听
     */
    object PluginPackageObserver : FileObserver(
        pluginDir.absolutePath,
        MODIFY or CLOSE_WRITE or MOVED_FROM or MOVED_TO or DELETE or DELETE_SELF or MOVE_SELF
    ) {
        override fun onEvent(event: Int, path: String?) {
            path?.also {
                File(pluginDir, path).apply {
                    logD("监测到插件变动", "event=$event path=$absolutePath")
                    when {
                        //下载安装缓存
                        path.startsWith("tmp_") -> if (event == CLOSE_WRITE)
                            pluginWorkScope.launch {
                                logD("安装插件缓存", path)
                                try {
                                    parsePluginInfo(this@apply.absolutePath)?.also {
                                        FileUri.getUriByFile(this@apply, true)
                                            ?.let { it1 -> installPlugin(it1, it) }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    delete()
                                }
                            }
                        //插件变动
                        path.startsWith("mediabox_plugin_") -> {
                            logD("更新插件", path)
                            scanPlugin()
                        }
                    }
                }
            }
        }
    }

}