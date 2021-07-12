package com.skyd.imomoe.util.html.source

import android.view.View

interface SniffingCallback {
    /**
     * 视频嗅探成功，只在{SniffingWebView}调用
     *
     * @param webView
     * @param html
     */
    fun onSniffingSuccess(webView: View?, html: String)

    /**
     * 视频嗅探失败成功
     *
     * @param webView
     * @param url
     * @param errorCode
     */
    fun onSniffingError(webView: View?, url: String?, errorCode: Int)
}