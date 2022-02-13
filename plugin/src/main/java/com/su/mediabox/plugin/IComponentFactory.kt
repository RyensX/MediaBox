package com.su.mediabox.plugin

import com.su.mediabox.plugin.interfaces.*
import com.su.mediabox.plugin.IComponentFactory.PluginSdkVersion
import java.lang.annotation.Inherited

/**
 * 组件工厂
 *
 * 每个插件都必须在包com.su.mediabox.plugin下继承本类实现 com.su.mediabox.plugin.ComponentFactory
 */
@PluginSdkVersion(1)
abstract class IComponentFactory {

    /**
     * 根据类型创建组件
     */
    abstract fun <T : IBase> create(clazz: Class<T>): T?

    /**
     * 插件SDK版本
     *
     * 在导入和载入时都会检查所在App支持的最低插件SDK版本，必须大于等于才能使用
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Inherited
    annotation class PluginSdkVersion(val version: Int)

    /**
     * 单例组件注解。
     *
     * 被标注次注解的组件在宿主里create时会加入组件池作为单例
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Inherited
    annotation class SingletonComponent
}