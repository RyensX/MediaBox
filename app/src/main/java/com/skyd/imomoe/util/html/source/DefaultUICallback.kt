package com.skyd.imomoe.util.html.source

import android.view.View

open class DefaultUICallback : SniffingUICallback {
    override fun onSniffingStart(webView: View?, url: String?) {}

    override fun onSniffingFinish(webView: View?, url: String?) {}

    override fun onSniffingSuccess(webView: View?, html: String) {}

    override fun onSniffingError(webView: View?, url: String?, errorCode: Int) {}
}