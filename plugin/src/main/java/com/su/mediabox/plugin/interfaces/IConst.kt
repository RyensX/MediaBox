package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.IComponentFactory

/**
 * 插件包的配置信息
 */
@IComponentFactory.SingletonComponent
interface IConst : IBase {
    companion object {
        const val implName = "Const"
    }

    /**
     * 唯一ID，用于标识插件
     */
    fun ID(): String

    /**
     * @return MAIN_URL
     */
    fun MAIN_URL(): String

    /**
     * 插件包的图标链接，不需要可空
     */
    fun ICON(): String? = null

    /**
     * @return 插件包的关于信息
     */
    fun about(): String {
        return MAIN_URL() + ""
    }

    /**
     * @return 插件包的版本名信息
     */
    fun versionName(): String? {
        return null
    }

    /**
     * @return 插件包的版本号信息
     */
    fun versionCode(): Int {
        return 0
    }
}