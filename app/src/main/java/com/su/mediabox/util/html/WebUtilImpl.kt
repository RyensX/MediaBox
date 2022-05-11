package com.su.mediabox.util.html

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.*
import com.su.mediabox.App
import com.su.mediabox.pluginapi.util.WebUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("SetJavaScriptEnabled")
object WebUtilImpl : WebUtil {

    private val globalWebView by lazy(LazyThreadSafetyMode.NONE) {
        WebView(App.context).apply {
            settings.apply {
                useWideViewPort = true
                allowFileAccess = true
                loadWithOverviewMode = true
                blockNetworkImage = false
                loadsImagesAutomatically = false
                javaScriptEnabled = true
                domStorageEnabled = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }

    override suspend fun getRenderedHtmlCode(
        url: String,
        callBackRegex: String,
        encoding: String,
        userAgentString: String?,
        actionJs: String?
    ): String =
        withContext(Dispatchers.Main) {
            Log.d("开始获取源码", url)
            val regexE = Regex(callBackRegex)
            var hasResult = false
            suspendCoroutine { con ->

                fun callBack(web: WebView) {
                    web.evaluateJavascript("${actionJs ?: ""} \n (function() { return document.documentElement.outerHTML })()") {
                        if (it.isNullOrEmpty())
                            con.resume("")
                        else if (!hasResult) {
                            hasResult = true
                            launch(Dispatchers.Default) {
                                val source = StringEscapeUtils.unescapeEcmaScript(it)
                                Log.d("获取源码成功", source)
                                con.resume(source)
                            }
                        }
                    }
                }

                globalWebView.settings.apply {
                    setUserAgentString(userAgentString)
                    defaultTextEncodingName = encoding
                }
                globalWebView.webViewClient = object : WebViewClient() {
                    //由于ajax存在可能不是真正完全加载
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        if (!hasResult && callBackRegex.isBlank())
                            callBack(view)
                    }

                    override fun onLoadResource(view: WebView, url: String) {
                        Log.d("链接", url)
                        if (callBackRegex.isNotBlank() && !hasResult && regexE.matches(url)) {
                            Log.d("匹配到回调", url)
                            callBack(view)
                        }
                        super.onLoadResource(view, url)
                    }
                }
                globalWebView.loadUrl(url)
            }
        }

    override suspend fun interceptResource(
        url: String,
        regex: String,
        userAgentString: String?,
        actionJs: String?
    ): String =
        withContext(Dispatchers.Main) {
            Log.d("开始拦截请求", "正则:$regex")
            var hasResult = false
            val regexE = Regex(regex)
            suspendCoroutine { con ->
                globalWebView.settings.userAgentString = userAgentString
                globalWebView.webViewClient = object : WebViewClient() {

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        actionJs?.let { view?.evaluateJavascript(it, null) }
                    }

                    override fun onLoadResource(view: WebView?, url: String) {
                        Log.d("链接", url)
                        if (!hasResult && regexE.matches(url)) {
                            Log.d("匹配到资源", url)
                            hasResult = true
                            view?.stopLoading()
                            view?.pauseTimers()
                            con.resume(url)
                        }
                        super.onLoadResource(view, url)
                    }
                }
                globalWebView.loadUrl(url)

            }
        }


}