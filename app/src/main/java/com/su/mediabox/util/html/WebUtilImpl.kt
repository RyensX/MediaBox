package com.su.mediabox.util.html

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import com.su.mediabox.util.logD
import android.webkit.*
import androidx.annotation.CallSuper
import com.su.mediabox.App
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.util.WebUtil
import com.su.mediabox.util.Text.containStrs
import kotlinx.coroutines.*
import org.apache.commons.text.StringEscapeUtils
import java.io.ByteArrayInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
object WebUtilImpl : WebUtil {

    private const val blobHook = """
        let origin = window.URL.createObjectURL
        window.URL.createObjectURL = function (t) {
            let blobUrl = origin(t)
            var xhr = new XMLHttpRequest()
            xhr.onload = function () {
                 window.blobHook.handleWrapper(xhr.responseText)
            }
            xhr.open('get', blobUrl)
            xhr.send();
            return blobUrl
        }
    """
    private var blobIntercept: BlobIntercept? = null

    private val globalWebView by lazy(LazyThreadSafetyMode.NONE) {
        WebView(App.context).apply {
            settings.apply {
                useWideViewPort = true
                allowFileAccess = true
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = false
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
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            //BlobHook回调
            addJavascriptInterface(object : BaseData() {
                @JavascriptInterface
                fun handleWrapper(blobTextData: String) {
                    if (blobIntercept?.handle(blobTextData) == true) {
                        //每次handle一次性
                        blobIntercept = null
                    }
                }
            }, "blobHook")
        }
    }

    /**
     * @param blockRes 屏蔽资源，加快抓取速度
     */
    private abstract class LightweightGettingWebViewClient(
        private val blockRes: Array<String> = arrayOf(
            ".css",
            ".mp4", ".ts",
            ".gif", ",jpg", ".png", ".webp"
        )
    ) : WebViewClient() {

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

        /**
         * 拦截无关资源文件
         */
        @CallSuper
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest?
        ) =
            //阻止无关资源加载，加快获取速度
            if (request?.url?.path?.containStrs(*blockRes) == true) {
                logD("禁止加载", request.url?.path ?: "")
                blockWebResourceRequest
            } else
                super.shouldInterceptRequest(view, request)
    }

    override suspend fun getRenderedHtmlCode(
        url: String,
        callBackRegex: String,
        encoding: String,
        userAgentString: String?,
        actionJs: String?,
        timeOut: Long
    ): String =
        withContext(Dispatchers.Main) {

            suspendCoroutine { con ->
                logD("开始获取源码", url)
                val regexE = Regex(callBackRegex)

                globalWebView.settings.apply {
                    setUserAgentString(userAgentString)
                    defaultTextEncodingName = encoding
                }
                globalWebView.webViewClient = object : LightweightGettingWebViewClient() {

                    lateinit var timeOutJob: Job
                    var hasResult = false

                    private fun callBack(web: WebView) {
                        timeOutJob.cancel()
                        hasResult = true

                        web.evaluateJavascript("${actionJs ?: ""} \n (function() { return document.documentElement.outerHTML })()") {
                            logD("脚本返回", url)
                            if (it.isNullOrEmpty())
                                con.resume("")
                            else {
                                launch(Dispatchers.Default) {
                                    val source = StringEscapeUtils.unescapeEcmaScript(it)
                                    logD("获取源码成功", source)
                                    con.resume(source)
                                    withContext(Dispatchers.Main) {
                                        web.apply {
                                            stopLoading()
                                            pauseTimers()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        timeOutJob = launch {
                            delay(timeOut)
                            if (!hasResult)
                                callBack(view)
                        }
                    }

                    //由于ajax存在可能不是真正完全加载
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        logD("页面载入完成", url ?: "")
                        if (!hasResult && callBackRegex.isBlank())
                            callBack(view)
                    }

                    //onLoadResource在shouldInterceptRequest禁止后不会再回调
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val resUrl = request?.url?.toString() ?: ""
                        logD("链接", resUrl)
                        if (callBackRegex.isNotBlank() && !hasResult && regexE.matches(resUrl)) {
                            logD("匹配到回调", resUrl)
                            callBack(view)
                        }
                        return super.shouldInterceptRequest(view, request)
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
        actionJs: String?,
        timeOut: Long
    ): String =
        withContext(Dispatchers.Main) {
            if (regex.isBlank())
                return@withContext ""
            logD("开始拦截请求", "正则:$regex")
            val regexE = Regex(regex)
            suspendCoroutine { con ->
                globalWebView.settings.userAgentString = userAgentString
                globalWebView.webViewClient = object : LightweightGettingWebViewClient() {

                    private lateinit var timeOutJob: Job
                    var hasResult = false

                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        timeOutJob = launch {
                            delay(timeOut)
                            if (!hasResult) {
                                hasResult = true
                                con.resume("")
                                view.stopLoading()
                                view.pauseTimers()
                            }
                        }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        actionJs?.let { view?.evaluateJavascript(it, null) }
                    }

                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val resUrl = request?.url?.toString() ?: ""
                        logD("链接", url)
                        if (!hasResult && regexE.matches(resUrl)) {
                            logD("匹配到资源", resUrl)
                            hasResult = true
                            view.stopLoading()
                            view.pauseTimers()
                            con.resume(resUrl)
                        }
                        return super.shouldInterceptRequest(view, request)
                    }
                }
                globalWebView.resumeTimers()
                globalWebView.loadUrl(url)

            }
        }

    private fun interface BlobIntercept {
        fun handle(blobTextData: String): Boolean
    }

    override suspend fun interceptBlob(
        url: String,
        regex: String,
        userAgentString: String?,
        actionJs: String?,
        timeOut: Long
    ): String = withContext(Dispatchers.Main) {
        if (regex.isBlank())
            return@withContext ""
        logD("开始拦截Blob", "正则:$regex")
        val regexE = Regex(regex)
        suspendCoroutine { con ->
            lateinit var timeOutJob: Job
            globalWebView.settings.userAgentString = userAgentString

            globalWebView.webViewClient = object : LightweightGettingWebViewClient() {

                var hasResult = false

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    //提前注入
                    view.evaluateJavascript(blobHook, null)
                    super.onPageStarted(view, url, favicon)
                    timeOutJob = launch {
                        delay(timeOut)
                        if (!hasResult) {
                            hasResult = true
                            con.resume("")
                            view.stopLoading()
                            view.pauseTimers()
                        }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    actionJs?.let { view?.evaluateJavascript(it, null) }
                }
            }

            blobIntercept = BlobIntercept {
                val test = regexE.containsMatchIn(it)
                logD("Blob数据", "res=$test\n$it", false)
                if (test) {
                    logD("匹配Blob数据", "\n" + it, false)
                    timeOutJob.cancel()
                    con.resume(it)
                }
                test
            }
            globalWebView.resumeTimers()
            globalWebView.loadUrl(url)
        }
    }

}