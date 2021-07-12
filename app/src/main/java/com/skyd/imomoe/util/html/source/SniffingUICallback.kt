package com.skyd.imomoe.util.html.source

import android.view.View

interface SniffingUICallback : SniffingCallback {
    /**
     * 开始视频嗅探
     * @param webView
     * @param url
     */
    fun onSniffingStart(webView: View?, url: String?)

    /**
     * 视频嗅探结束
     * @param webView
     * @param url
     */
    fun onSniffingFinish(webView: View?, url: String?)
}