package com.skyd.imomoe.model

import android.util.LruCache
import com.skyd.imomoe.App
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.PluginManager
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.sharedPreferences
import com.su.mediabox.plugin.interfaces.IConst
import com.su.mediabox.plugin.interfaces.IUtil
import dalvik.system.DexClassLoader
import java.io.File

object DataSourceManager {

    const val DEFAULT_DATA_SOURCE = ""

    var dataSourceName: String =
        App.context.sharedPreferences().getString("dataSourceName", DEFAULT_DATA_SOURCE)
            ?: DEFAULT_DATA_SOURCE
        get() {
            return if (field.isBlank() && App.context.sharedPreferences()
                    .getBoolean("customDataSource", false)
            ) {
                App.context.sharedPreferences().editor { putBoolean("customDataSource", false) }
                "CustomDataSource.jar"
            } else field
        }
        set(value) {
            field = value
            App.context.sharedPreferences().editor { putString("dataSourceName", value) }
        }

    // 第一个是传入的接口，第二个是实现类
    private val cache: LruCache<Class<*>, Class<*>> = LruCache(10)
    private val singletonCache: LruCache<Class<*>, Any> = LruCache(5)

    fun getJarDirectory(): String {
        return PluginManager.getPluginsDirectory()
    }

    /**
     * 在更换数据源后必须调用此方法
     */
    fun clearCache() {
        cache.evictAll()
        singletonCache.evictAll()
    }


}