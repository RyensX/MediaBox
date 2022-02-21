package com.su.mediabox.util.html.source.web

import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.su.mediabox.util.html.source.Util

class GettingWebChromeClient(private val mClient: GettingWebViewClient) : WebChromeClient() {
    override fun onJsConfirm(webView: WebView, s: String, s1: String, jsResult: JsResult): Boolean {
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