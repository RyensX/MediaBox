package com.su.mediabox.util.html.source

import android.view.View

interface GettingCallback {
    /**
     * 获取源代码成功，只在{GettingWebView}调用
     *
     * @param webView
     * @param html
     */
    fun onGettingSuccess(webView: View?, html: String)

    /**
     * 获取源代码失败
     *
     * @param webView
     * @param url
     * @param errorCode
     */
    fun onGettingError(webView: View?, url: String?, errorCode: Int)
}