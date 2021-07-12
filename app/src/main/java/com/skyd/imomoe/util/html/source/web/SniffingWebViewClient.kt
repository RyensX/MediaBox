package com.skyd.imomoe.util.html.source.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.skyd.imomoe.util.html.source.SniffingCallback
import com.skyd.imomoe.util.html.source.SniffingUICallback
import com.skyd.imomoe.util.html.source.Util

class SniffingWebViewClient(
    private val mWebView: WebView?,
    private val mURL: String,
    private val mHeader: Map<String, String>,
    private val mCallback: SniffingCallback?
) : WebViewClient() {
    private var isCompleteLoader = true
    private val mH = Handler(Looper.getMainLooper())
    private val mHtmlSource: String? = null
    private var mLastStartTime: Long = 0
    private var mLastEndTime = System.currentTimeMillis()
    private var mFinished: FinishedRunnable? = null
    private var mConnTimeout: TimeOutRunnable? = null
    private var mReadTimeout: TimeOutRunnable? = null
    private val mJSRunnable: ParserHtmlRunnable? = null
    private var mConnTimeOut = 20 * 1000.toLong()
    private var mReadTimeOut = 45 * 1000.toLong()
    private var mFinishedTimeOut: Long = 800
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("http")) view.loadUrl(url, mHeader)
        return true
    }

    fun setConnTimeOut(connTimeOut: Long) {
        mConnTimeOut = connTimeOut
    }

    fun setFinishedTimeOut(mFinishedTimeOut: Long) {
        this.mFinishedTimeOut = mFinishedTimeOut
    }

    fun setReadTimeOut(readTimeOut: Long) {
        mReadTimeOut = readTimeOut
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (mLastEndTime - mLastStartTime <= 500 || !isCompleteLoader) { // 基本上是302 重定向才会走这段逻辑
//            LogUtil.e("SniffingUtil", "onStart( 302 )  --> " + url);
            mFinished?.let {
                mH.removeCallbacks(it)
            }
            return
        }
        if (mConnTimeout != null) {
            mH.removeCallbacks(mConnTimeout!!)
        }
        mH.postDelayed(
            TimeOutRunnable(
                view,
                url,
                TYPE_CONN
            ).also { mConnTimeout = it }, mConnTimeOut
        )
        //        LogUtil.e("SniffingUtil", "onStart(onPageStarted)  --> " + url);
        onSniffingStart(view, url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        mLastEndTime = System.currentTimeMillis()
        mH.postDelayed(FinishedRunnable(view, url).also { mFinished = it }, mFinishedTimeOut)
    }

    //    @Override
    //    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    //        try {
    ////            LogUtil.e("SniffingUtil", "shouldInterceptRequest(URL)  --> " + url);
    //            if(url.lastIndexOf(".") < url.length() - 5){
    //                Object[] content = Util.getContent(url);
    //                String s = content[1].toString();
    //                if(s.toLowerCase().contains("video") || s.toLowerCase().contains("mpegurl")){
    //                    mVideos.add(new SniffingVideo(url,"m3u8",(int) content[0],"m3u8"));
    //                }
    //            }else if (mFilter != null) {
    //                SniffingVideo video = mFilter.onFilter(view, url);
    //                if (video != null) mVideos.add(video);
    //            }
    //        } catch (Throwable e) {
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }
    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
//            LogUtil.e("SniffingUtil", "onReceivedError(ReceivedError)  --> " + failingUrl);
        onSniffingError(view, failingUrl, RECEIVED_ERROR)
        onSniffingFinish(view, failingUrl)
    }

    override fun onReceivedSslError(
        webView: WebView,
        sslErrorHandler: SslErrorHandler,
        sslError: SslError
    ) {
        sslErrorHandler.proceed() //證書不對的時候，繼續加載
    }

    fun onSniffingStart(webView: View?, url: String?) {
        isCompleteLoader = false
        mLastStartTime = System.currentTimeMillis()
        if (mReadTimeout != null) {
            mH.removeCallbacks(mReadTimeout!!)
        }
        mH.postDelayed(
            TimeOutRunnable(
                webView as WebView,
                url,
                TYPE_READ
            ).also { mReadTimeout = it }, mReadTimeOut
        )
        if (mCallback is SniffingUICallback) {
            mH.post { mCallback.onSniffingStart(webView, url) }
        }
    }

    fun onSniffingError(webView: View?, url: String?, errorCode: Int) {
        if (mCallback != null) {
            mH.post { mCallback.onSniffingError(webView, url, errorCode) }
        }
    }

    fun onSniffingFinish(webView: View?, url: String?) {
        isCompleteLoader = true
        mH.removeCallbacks(mReadTimeout!!)
        mReadTimeout = null
        if (mCallback is SniffingUICallback) {
            mH.post { mCallback.onSniffingFinish(webView, url) }
        }
    }

    private inner class ParserHtmlRunnable(private val view: WebView, private val method: String) :
        Runnable {
        override fun run() {
            Util.evalScript(view, method)
        }

    }

    //一次网页加载结束
    private inner class FinishedRunnable(private val view: WebView, private val url: String) :
        Runnable {
        override fun run() {
            if (mConnTimeout == null) return
            mH.removeCallbacks(mConnTimeout!!)
            mConnTimeout = null
            Log.i("SniffingUtil", "一次网页加载结束 --> $url")
            onSniffingFinish(view, url)
            Util.getHtmlSource(view)
        }
    }

    //一次网页加载，解析超时
    private inner class TimeOutRunnable(
        private val view: WebView?,
        private val url: String?,
        private val type: Int
    ) :
        Runnable {
        override fun run() {
            //加载网页超时了
            if (type == TYPE_CONN) {
                Log.e(
                    "SniffingUtil",
                    "ConnTimeOutRunnable( postDelayed  【alert ，confirm】 )  --> $url"
                )
                if (mConnTimeout == null) return
                mH.removeCallbacks(mConnTimeout!!)
                mConnTimeout = null
                //                mH.postDelayed(new ParserHtmlRunnable(view, "alert"), 5000);
//                mH.postDelayed(mJSRunnable = new ParserHtmlRunnable(view, "confirm"), 8000);
            } else if (type == TYPE_READ) {
                Log.e("SniffingUtil", "ReadTimeOutRunnable(SUCCESS)  --> $url")
                onSniffingFinish(view, url)
            }
        }

    }

    companion object {
        const val READ_TIME_OUT = 1
        const val RECEIVED_ERROR = 2
        const val NOT_FIND = 3
        const val CONNECTION_ERROR = 4
        const val CONTENT_ERROR = 5
        const val TYPE_CONN = 0
        const val TYPE_READ = 1
    }

}