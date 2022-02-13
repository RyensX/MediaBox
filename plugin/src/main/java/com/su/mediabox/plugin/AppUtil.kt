package com.su.mediabox.plugin

import android.content.Context
import com.su.mediabox.plugin.interfaces.IRouteProcessor

object AppUtil {

    var appContext: Context? = null
        private set

    var appProcessor: IRouteProcessor? = null
        private set

    /**
     * 由App初始化
     */
    fun init(applicationContext: Context, appProcessor: IRouteProcessor) {
        appContext = applicationContext
        this.appProcessor = appProcessor
    }

}