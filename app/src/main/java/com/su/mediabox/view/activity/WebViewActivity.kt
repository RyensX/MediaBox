package com.su.mediabox.view.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import com.su.mediabox.util.logD
import android.webkit.*
import androidx.appcompat.content.res.AppCompatResources
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityWebViewBinding
import com.su.mediabox.pluginapi.action.WebBrowserAction
import com.su.mediabox.util.Util.openUrl
import com.su.mediabox.util.getAction
import com.su.mediabox.util.logE
import com.su.mediabox.util.viewBind

class WebViewActivity : BasePluginActivity() {

    private val mBinding by viewBind(ActivityWebViewBinding::inflate)
    private lateinit var mainUrl: String

    private val TAG = "网页浏览"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainUrl = getAction<WebBrowserAction>()?.url ?: run {
            finish()
            return
        }

        initWebView()

        mBinding.atbWebViewActivityToolbar.apply {

            AppCompatResources.getDrawable(this@WebViewActivity, R.drawable.ic_baseline_open_in_new)
                ?.apply {
                    setTint(Color.WHITE)
                    addButton(this)
                    setButtonClickListener(0) {
                        openUrl(mainUrl)
                    }
                }

            setBackButtonClickListener { finish() }
        }

        mBinding.wvWeb.loadUrl(mainUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        mBinding.wvWeb.apply {

            settings.apply {
                useWideViewPort = true
                allowFileAccess = true
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                domStorageEnabled = true
                databaseEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                allowFileAccess = true
                setSupportZoom(true)
                allowContentAccess = true
                setSupportMultipleWindows(true)
            }

            scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
            isHorizontalScrollBarEnabled = false
            isHorizontalFadingEdgeEnabled = false
            isVerticalFadingEdgeEnabled = false
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    mBinding.atbWebViewActivityToolbar.titleText = title
                }
            }
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: ""
                    if (url.startsWith("https") || url.startsWith("http"))
                        view.loadUrl(url)
                    else {
                        logD(TAG, "拦截：$url")
                        return true
                    }
                    return false
                }

                @SuppressLint("WebViewClientOnReceivedSslError")
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    error?.toString()?.also {
                        logE("网页加载错误", it)
                    }
                    handler?.proceed()
                }
            }
        }.settings.apply {
            javaScriptEnabled = true
        }
    }

    override fun onBackPressed() {
        mBinding.wvWeb.apply {
            logD("链接", "原始：$mainUrl org：$originalUrl")
            //有些网页重定向了无法有效判断
            if (canGoBack() && !(originalUrl == mainUrl || originalUrl == "$mainUrl/index"))
                goBack()
            else
                super.onBackPressed()
        }
    }

}