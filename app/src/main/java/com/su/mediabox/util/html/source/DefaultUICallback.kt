package com.su.mediabox.util.html.source

import android.view.View

open class DefaultUICallback : GettingUICallback {
    override fun onGettingStart(webView: View?, url: String?) {}

    override fun onGettingFinish(webView: View?, url: String?) {}

    override fun onGettingSuccess(webView: View?, html: String) {}

    override fun onGettingError(webView: View?, url: String?, errorCode: Int) {}
}