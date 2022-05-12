package com.su.mediabox.util.html

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.webkit.*
import com.su.mediabox.App
import com.su.mediabox.pluginapi.util.WebUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import java.io.ByteArrayInputStream
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
                javaScriptCanOpenWindowsAutomatically = false
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
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }
    }

    private abstract class LightweightWebViewClient : WebViewClient() {

        private val blockWebResourceRequest =
            WebResourceResponse("text/html", "utf-8", ByteArrayInputStream("".toByteArray()))

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            handler?.proceed()
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ) =
            //阻止无关资源加载，加快获取速度
            if (request?.url?.path?.contains(".css") == true)
                blockWebResourceRequest
            else
                super.shouldInterceptRequest(view, request)
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
                    hasResult = true
                    web.apply {
                        stopLoading()
                        pauseTimers()
                    }
                    web.evaluateJavascript("${actionJs ?: ""} \n (function() { return document.documentElement.outerHTML })()") {
                        Log.d("脚本返回", url)
                        if (it.isNullOrEmpty())
                            con.resume("")
                        else {
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
                globalWebView.webViewClient = object : LightweightWebViewClient() {

                    //由于ajax存在可能不是真正完全加载
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("页面载入完成", url ?: "")
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
                globalWebView.resumeTimers()
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
                globalWebView.webViewClient = object : LightweightWebViewClient() {

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
                globalWebView.resumeTimers()
                globalWebView.loadUrl(url)

            }
        }


}