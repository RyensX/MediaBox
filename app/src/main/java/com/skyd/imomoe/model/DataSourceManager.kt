package com.skyd.imomoe.model

import com.skyd.imomoe.App
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

    fun getJarPath(): String {
        return App.context.getExternalFilesDir(null)
            .toString() + "/DataSourceJar/CustomDataSource.jar"
    }

    fun <T> getBinaryName(clazz: Class<T>): String {
        return "com.skyd.imomoe.model.impls.Custom${clazz.getDeclaredField("implName").get(null)}"
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(clazz: Class<T>): T? {
        // 如果不使用自定义数据，直接返回null
        if (!useCustomDataSource) return null
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
        try {
            // 该方法将Class文件加载到内存时,并不会执行类的初始化,直到这个类第一次使用时才进行初始化.该方法因为需要得到一个ClassLoader对象
            val clz = classLoader.loadClass(getBinaryName(clazz))
            o = clz.newInstance() as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return o
    }
}