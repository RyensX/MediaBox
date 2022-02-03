package com.su.mediabox.plugin.interfaces

import android.app.Activity
import android.app.Fragment
import android.content.Context

/**
 * 界面跳转处理接口
 */
interface IRouteProcessor {
    /**
     * 处理根据actionUrl跳转
     *
     * @param context   Context
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    fun process(context: Context, actionUrl: String): Boolean

    /**
     * 处理根据actionUrl跳转
     *
     * @param activity  Activity
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    fun process(activity: Activity, actionUrl: String): Boolean

    /**
     * 处理根据actionUrl跳转
     *
     * @param fragment  Fragment
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    fun process(fragment: Fragment, actionUrl: String): Boolean {
        val activity: Activity? = fragment.activity
        return activity?.let { process(it, actionUrl) } ?: false
    }

    companion object {
        const val implName = "RouteProcessor"
    }
}