package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import com.skyd.imomoe.databinding.ActivityWebViewBinding


class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {
    private lateinit var url: String
    private lateinit var headers: HashMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = intent.getStringExtra("url") ?: ""
        intent.getSerializableExtra("headers").let {
            headers = if (it == null) {
                HashMap()
            } else {
                it as HashMap<String, String>
            }
        }
        mBinding.atbWebViewActivityToolbar.setBackButtonClickListener { finish() }
        mBinding.wvWebViewActivity.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("https") || url.startsWith("http")) {
                    view.loadUrl(url)
                } else {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                return true
            }
        }
        initSettings()

        mBinding.wvWebViewActivity.loadUrl(url, headers)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        mBinding.wvWebViewActivity.settings.run {
            setAllowFileAccess(true)
            setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
            setSupportZoom(true)
            setBuiltInZoomControls(true)
            setUseWideViewPort(true)
            setSupportMultipleWindows(false)
            setAppCacheEnabled(true)
            setDomStorageEnabled(true)
            setJavaScriptEnabled(true)
            setGeolocationEnabled(true)
            setAppCacheMaxSize(Long.MAX_VALUE)
            setAppCachePath(getDir("appcache", 0).path)
            setDatabasePath(getDir("databases", 0).path)
            setGeolocationDatabasePath(
                getDir("geolocation", 0)
                    .path
            )
            setPluginState(WebSettings.PluginState.ON_DEMAND)
            setLoadWithOverviewMode(true)
            setCacheMode(WebSettings.LOAD_NO_CACHE)
            val mUserAgent: String = getUserAgentString()
            setUserAgentString("$mUserAgent App/AppName")
            syncCookie()
            setUseWideViewPort(true)
            setLoadWithOverviewMode(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
            } else {
                setCacheMode(WebSettings.LOAD_DEFAULT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setDisplayZoomControls(false)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setLoadsImagesAutomatically(true)
            } else {
                setLoadsImagesAutomatically(false)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) CookieManager.getInstance()
                .setAcceptThirdPartyCookies(mBinding.wvWebViewActivity, true)

            mBinding.wvWebViewActivity.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY)
            mBinding.wvWebViewActivity.setHorizontalScrollBarEnabled(false)
            mBinding.wvWebViewActivity.setHorizontalFadingEdgeEnabled(false)
            mBinding.wvWebViewActivity.setVerticalFadingEdgeEnabled(false)

            mBinding.wvWebViewActivity.requestFocus()
//            defaultTextEncodingName = "utf-8"
//            cacheMode = WebSettings.LOAD_DEFAULT
//            useWideViewPort = true
//            allowFileAccess = true
//            setSupportZoom(true)
//            allowContentAccess = true
//            javaScriptEnabled = true
//            domStorageEnabled = true
//            pluginState = WebSettings.PluginState.ON// 可以使用插件
//            setSupportMultipleWindows(true)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mixedContentMode = 0
//            }
//            mediaPlaybackRequiresUserGesture = true
//            allowFileAccessFromFileURLs = true
//            allowUniversalAccessFromFileURLs = true
//            javaScriptCanOpenWindowsAutomatically = true
//            loadsImagesAutomatically = true
//            setAppCacheEnabled(true)
//            setAppCachePath(cacheDir.absolutePath)
//            databaseEnabled = true
//            setAppCachePath(getDir("appCache", 0).path)
//            setGeolocationDatabasePath(getDir("database", 0).path)
//            setGeolocationDatabasePath(getDir("geolocation", 0).path)
//            setGeolocationEnabled(true)
//            val instance = CookieManager.getInstance()
//            instance.setAcceptCookie(true)
//            if (Build.VERSION.SDK_INT >= 21) {
//                instance.setAcceptThirdPartyCookies(mBinding.wvWebViewActivity, true)
//            }


        }
    }

    private fun syncCookie() {
        CookieSyncManager.createInstance(this)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().sync()
    }

    override fun getBinding(): ActivityWebViewBinding =
        ActivityWebViewBinding.inflate(layoutInflater)
}