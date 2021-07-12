package com.skyd.imomoe.util.html.source.web

import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.skyd.imomoe.util.html.source.Util

class SniffingWebChromeClient(private val mClient: SniffingWebViewClient) : WebChromeClient() {
    override fun onJsConfirm(
        webView: WebView,
        s: String,
        s1: String,
        jsResult: JsResult
    ): Boolean {
        if (s1.contains(Util.HTMLFLAG)) {
//            mClient.parserHtml(webView, s, s1);
            jsResult.cancel()
            return true
        }
        return super.onJsConfirm(webView, s, s1, jsResult)
    }

    override fun onJsAlert(
        webView: WebView,
        s: String,
        s1: String,
        jsResult: JsResult
    ): Boolean {
        return if (s1.contains(Util.HTMLFLAG)) {
            onJsConfirm(webView, s, s1, jsResult)
        } else super.onJsAlert(webView, s, s1, jsResult)
    }

}