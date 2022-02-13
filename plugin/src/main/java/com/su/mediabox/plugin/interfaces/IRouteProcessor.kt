package com.su.mediabox.plugin.interfaces

/**
 * 界面跳转处理接口
 */
@Deprecated("即将移除，不再由插件处理路由逻辑（插件根据需要构造actionUrl交由宿主处理）")
interface IRouteProcessor : IBase {

    /**
     * 处理根据actionUrl跳转
     *
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    fun process(actionUrl: String): Boolean

    companion object {
        const val implName = "RouteProcessor"
    }
}