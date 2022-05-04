package com.su.mediabox.view.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.content.res.AppCompatResources
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityWebViewBinding
import com.su.mediabox.pluginapi.v2.action.WebBrowserAction
import com.su.mediabox.util.Util.openUrl
import com.su.mediabox.util.getAction


class WebViewActivity : BasePluginActivity<ActivityWebViewBinding>() {

    private lateinit var mainUrl: String

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
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.startsWith("https") || url.startsWith("http")) {
                        view.loadUrl(url)
                    } else try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                    return true
                }
            }
        }.settings.apply {
            allowFileAccess = true
            setSupportZoom(true)
            builtInZoomControls = true
            useWideViewPort = true
            setSupportMultipleWindows(false)
            domStorageEnabled = true
            javaScriptEnabled = true
            setGeolocationEnabled(true)
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            displayZoomControls = false
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            //如果某些网站需要验证则推荐使用WebBrowserAction打开页面手动验证后插件通过CookieManager.getInstance()获取对应cookies
            CookieManager.getInstance()
                .setAcceptThirdPartyCookies(mBinding.wvWeb, true)
        }
    }

    override fun onBackPressed() {
        mBinding.wvWeb.apply {
            Log.d("链接", "原始：$mainUrl org：$originalUrl")
            //有些网页重定向了无法有效判断
            if (canGoBack() && !(originalUrl == mainUrl || originalUrl == "$mainUrl/index"))
                goBack()
            else
                super.onBackPressed()
        }
    }

    override fun getBinding(): ActivityWebViewBinding =
        ActivityWebViewBinding.inflate(layoutInflater)
}