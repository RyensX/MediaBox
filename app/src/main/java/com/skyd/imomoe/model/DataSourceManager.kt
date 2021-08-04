package com.skyd.imomoe.model

import android.util.LruCache
import com.skyd.imomoe.App
import com.skyd.imomoe.model.interfaces.IConst
import com.skyd.imomoe.model.interfaces.IRouterProcessor
import com.skyd.imomoe.model.interfaces.IUtil
import com.skyd.imomoe.util.debug
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences
import dalvik.system.DexClassLoader


object DataSourceManager {
    var useCustomDataSource: Boolean
        get() {
            return App.context.sharedPreferences("App").getBoolean("customDataSource", false)
        }
        set(value) {
            App.context.sharedPreferences("App").editor { putBoolean("customDataSource", value) }
        }

    // 第一个是传入的接口，第二个是实现类
    private val cache: LruCache<Class<*>, Class<*>> = LruCache(20)
    private val singletonCache: LruCache<Class<*>, Any> = LruCache(10)

    fun getJarPath(): String {
        return App.context.getExternalFilesDir(null)
            .toString() + "/DataSourceJar/CustomDataSource.jar"
    }

    fun <T> getBinaryName(clazz: Class<T>): String {
        return "com.skyd.imomoe.model.impls.custom.Custom${clazz.getDeclaredField("implName")
            .get(null)}"
    }

    fun getUtil(): IUtil? {
        singletonCache[IUtil::class.java].let {
            if (it != null && it is IUtil) return it
        }
        return create(IUtil::class.java).apply {
            if (this != null) singletonCache.put(IUtil::class.java, this)
        }
    }

    fun getRouterProcessor(): IRouterProcessor? {
        singletonCache[IRouterProcessor::class.java].let {
            if (it != null && it is IRouterProcessor) return it
        }
        return create(IRouterProcessor::class.java).apply {
            if (this != null) singletonCache.put(IRouterProcessor::class.java, this)
        }
    }

    fun getConst(): IConst? {
        singletonCache[IConst::class.java].let {
            if (it != null && it is IConst) return it
        }
        return create(IConst::class.java).apply {
            if (this != null) singletonCache.put(IConst::class.java, this)
        }
    }

    /**
     * 在更换数据源后必须调用此方法
     */
    fun clearCache() {
        cache.evictAll()
        singletonCache.evictAll()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(clazz: Class<T>): T? {
        // 如果不使用自定义数据，直接返回null
        if (!useCustomDataSource) return null
        cache[clazz]?.let {
            return it.newInstance() as T
        }
        /**
         * 参数1 jarPath：待加载的jar文件路径，注意权限。jar必须是含dex的jar（dx --dex --output=dest.jar source.jar）
         * 参数2 optimizedDirectory：解压后的dex存放位置，此位置一定要是可读写且仅该应用可读写
         * 参数3 libraryPath：指向包含本地库(so)的文件夹路径，可以设为null
         * 参数4 parent：父级类加载器，一般可以通过Context.getClassLoader获取
         */
        val optimizedDirectory = App.context.getExternalFilesDir(null).toString() + "/DataSourceDex"
        val classLoader =
            DexClassLoader(getJarPath(), optimizedDirectory, null, App.context.classLoader)
        var o: T? = null
        var clz: Class<*>? = null
        try {
            // 该方法将Class文件加载到内存时,并不会执行类的初始化,直到这个类第一次使用时才进行初始化.该方法因为需要得到一个ClassLoader对象
            clz = classLoader.loadClass(getBinaryName(clazz))
            o = clz.newInstance() as T
        } catch (e: Exception) {
            e.printStackTrace()
//            debug {
//                o = getTestClass(clazz)
//            }
        }
        if (clz != null) cache.put(clazz, clz)
        return o
    }

//    private fun <T> getTestClass(clazz: Class<T>): T? {
//        var o: T? = null
//        TestClass.classMap[clazz.simpleName].let {
//            if (it != null) o = it.newInstance() as T
//        }
//        return o
//    }

}