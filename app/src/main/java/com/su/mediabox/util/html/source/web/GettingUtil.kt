package com.su.mediabox.util.html.source.web

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import com.su.mediabox.util.html.source.GettingCallback
import com.su.mediabox.util.html.source.GettingUICallback
import com.su.mediabox.pluginapi.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import kotlin.collections.HashMap

class GettingUtil private constructor() {
    private var mWebView: GettingWebView? = null
    private var mUrl: String = ""
    private var mCallbackChange = false
    private lateinit var mCallback: GettingCallback
    private var mHeader: MutableMap<String, String> = HashMap()
    private var mActivity: SoftReference<Activity>? = null
    private var mConnTimeOut = 16 * 1000L
    private var mReadTimeOut = 20 * 1000L
    private var mFinishedTimeOut: Long = 800
    private lateinit var mClient: GettingWebViewClient

    @Synchronized
    fun setFinishedTimeOut(mFinishedTimeOut: Long): GettingUtil {
        this.mFinishedTimeOut = mFinishedTimeOut
        return this
    }

    @UiThread
    @Synchronized
    fun releaseWebView() {
        GlobalScope.launch(Dispatchers.Main) {
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
    }

    @Synchronized
    fun release() {
        mActivity = null
    }

    @Synchronized
    fun referer(referer: String): GettingUtil {
        mHeader["Referer"] = referer
        mHeader["User-Agent"] = Constant.Request.getRandomUserAgent()
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
    fun start(callback: GettingCallback, ua: String? = null) {
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
                mWebView = GettingWebView(activity)
            }
            mWebView?.settings?.userAgentString = ua
            mWebView?.setCallBack(mCallback)
            if (mCallbackChange && mWebView != null) {
                mCallbackChange = false
                mClient = GettingWebViewClient(
                    mWebView, mUrl,
                    mHeader, mCallback
                )
                mClient.setReadTimeOut(mReadTimeOut)
                mClient.setConnTimeOut(mConnTimeOut)
                mClient.setFinishedTimeOut(mFinishedTimeOut)
                val chromeClient = GettingWebChromeClient(mClient)
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
                    mWebView?.loadUrl(mUrl, mHeader)
                }
            } else {
                mCallback.onGettingError(mWebView, mUrl, -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mCallback.onGettingError(mWebView, mUrl, -1)
        }
    }

    fun url(url: String): GettingUtil {
        mUrl = url
        return this
    }

    fun activity(activity: Activity): GettingUtil {
        mWebView?.settings?.userAgentString = null
        mActivity = SoftReference(activity)
        return this
    }

    fun header(map: MutableMap<String, String>): GettingUtil {
        mHeader.putAll(map)
        return this
    }

    fun connTimeOut(connTimeOut: Long): GettingUtil {
        mConnTimeOut = connTimeOut
        mClient.setConnTimeOut(connTimeOut)
        return this
    }

    fun readTimeOut(readTimeOut: Long): GettingUtil {
        mReadTimeOut = readTimeOut
        mClient.setReadTimeOut(readTimeOut)
        return this
    }

    fun onGettingStart(webView: View?, url: String?) {
        if (mCallback is GettingUICallback) {
            (mCallback as GettingUICallback).onGettingStart(webView, url)
        }
    }

    fun onGettingFinish(webView: View?, url: String?) {
        if (mCallback is GettingUICallback) {
            (mCallback as GettingUICallback).onGettingFinish(webView, url)
        }
        releaseAll()
    }

    companion object {
        val instance: GettingUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GettingUtil()
        }
        var WEB_VIEW_DEBUG = false
    }
}