package com.skyd.imomoe.util.html.source.web

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.util.html.source.SniffingCallback
import com.skyd.imomoe.util.html.source.SniffingUICallback
import com.skyd.imomoe.util.html.source.web.SniffingWebView.HtmlSourceListener
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.HashMap

class SniffingUtil private constructor() {
    private var mWebView: SniffingWebView? = null
    private var mUrl: String = ""
    private var mCallbackChange = false
    private lateinit var mCallback: SniffingCallback
    private var mHeader: MutableMap<String, String> = HashMap()
    private var mActivity: SoftReference<Activity>? = null
    private var mConnTimeOut = 20 * 1000.toLong()
    private var mReadTimeOut = 45 * 1000.toLong()
    private var mFinishedTimeOut: Long = 800
    private lateinit var mClient: SniffingWebViewClient

    @Synchronized
    fun setFinishedTimeOut(mFinishedTimeOut: Long): SniffingUtil {
        this.mFinishedTimeOut = mFinishedTimeOut
        return this
    }

    @UiThread
    @Synchronized
    fun releaseWebView() {
        try {
            mWebView?.let {
                it.removeAllViews()
                if (it.parent != null) {
                    val viewGroup = it.parent as ViewGroup
                    viewGroup.removeView(it)
                }
                it.destroy()
            }
            mWebView = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun release() {
        mActivity = null
    }

    @Synchronized
    fun referer(referer: String): SniffingUtil {
        mHeader["Referer"] = referer
        mHeader["User-Agent"] =
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"
        mHeader["Accept"] =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
        mHeader["Accept-Encoding"] = "gzip, deflate"
        return this
    }

    @Synchronized
    fun releaseAll() {
        releaseWebView()
        release()
    }

    @Synchronized
    fun start(callback: SniffingCallback) {
        try {
            mCallback = callback
//            if (mActivity == null) {
//                if (mAutoRelease) {
//                    onSniffingError(mWebView, mUrl, -1);
//                } else if (mCallback != null) {
//                    mCallback.onSniffingError(mWebView, mUrl, -1);
//                }
//                return;
//            }
            val activity = mActivity!!.get()
            if (mWebView == null && activity != null) {
                mCallbackChange = true
                mWebView = SniffingWebView(activity)
            }
            mWebView?.setCallBack(mCallback)
            if (mCallbackChange && mWebView != null) {
                mCallbackChange = false
                mClient = SniffingWebViewClient(
                    mWebView, mUrl,
                    mHeader, mCallback
                )
                mClient.setReadTimeOut(mReadTimeOut)
                mClient.setConnTimeOut(mConnTimeOut)
                mClient.setFinishedTimeOut(mFinishedTimeOut)
                val chromeClient = SniffingWebChromeClient(mClient)
                mWebView?.webViewClient = mClient
                mWebView?.webChromeClient = chromeClient
            }
            if (mWebView != null && activity != null) {
                if (mWebView?.parent == null) {
                    val mainView =
                        activity.findViewById<View>(R.id.content) as ViewGroup
                    if (WEB_VIEW_DEBUG) {
                        val display = activity.windowManager.defaultDisplay
                        mWebView?.layoutParams = ViewGroup.LayoutParams(
                            display.width / 2,
                            display.height / 2
                        )
                    } else {
                        mWebView?.layoutParams = ViewGroup.LayoutParams(1, 1)
                    }
                    mainView.addView(mWebView)
                }
                mWebView?.post {
                    mWebView?.loadUrl(mUrl!!, mHeader)
                }
            } else {
                mCallback.onSniffingError(mWebView, mUrl, -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mCallback.onSniffingError(mWebView, mUrl, -1)
        }
    }

    fun url(url: String): SniffingUtil {
        mUrl = url
        return this
    }

    fun activity(activity: Activity): SniffingUtil {
        mActivity = SoftReference(activity)
        return this
    }

    fun header(map: MutableMap<String, String>): SniffingUtil {
        mHeader.putAll(map)
        return this
    }

    fun connTimeOut(connTimeOut: Long): SniffingUtil {
        mConnTimeOut = connTimeOut
        mClient.setConnTimeOut(connTimeOut)
        return this
    }

    fun readTimeOut(readTimeOut: Long): SniffingUtil {
        mReadTimeOut = readTimeOut
        mClient.setReadTimeOut(readTimeOut)
        return this
    }

    fun onSniffingStart(webView: View?, url: String?) {
        if (mCallback is SniffingUICallback) {
            (mCallback as SniffingUICallback).onSniffingStart(webView, url)
        }
    }

    fun onSniffingFinish(webView: View?, url: String?) {
        if (mCallback is SniffingUICallback) {
            (mCallback as SniffingUICallback).onSniffingFinish(webView, url)
        }
        releaseAll()
    }

    companion object {
        val instance: SniffingUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SniffingUtil()
        }
        var WEB_VIEW_DEBUG = false
    }
}