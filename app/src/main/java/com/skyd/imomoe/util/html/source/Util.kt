package com.skyd.imomoe.util.html.source

import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import com.skyd.imomoe.util.logE
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*
import kotlin.jvm.Throws

object Util {
    const val HTMLFLAG = "<GettingVideo>GettingVideo</GettingVideo>"
    fun getContent(url: String): Array<Any?> {
        val objects = arrayOfNulls<Any>(2)
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = URL(url).openConnection() as HttpURLConnection
            if (url.startsWith("https")) {
                val https =
                    urlConnection as HttpsURLConnection?
                // 方式一，相信所有
                trustAllHosts(https)
                // 方式二，覆盖默认验证方法
                https!!.hostnameVerifier
                // 方式三，不校验
                https.hostnameVerifier = DO_NOT_VERIFY
            }
            urlConnection.requestMethod = "HEAD"
            val responseCode = urlConnection.responseCode
            if (responseCode == 200) {
                objects[0] = urlConnection.contentLength
                objects[1] = urlConnection.contentType
            }
            logE("Util", "getContent code = $responseCode")
        } catch (e: Exception) {
            e.printStackTrace()
            logE("Util", "getContent error = $e")
        } finally {
            urlConnection?.disconnect()
        }
        if (objects[0] == null) objects[0] = -1
        if (objects[1] == null) objects[1] = ""
        return objects
    }

    /**
     * 對url進行包裝
     *
     * @param url
     * @return
     */
    fun warpUrl(mURL: String, url: String): String {
        var url = url
        try {
            url = if (url.startsWith("//")) {
                "http:$url"
            } else if (url.startsWith("/")) {
                val split = mURL.split("/".toRegex()).toTypedArray()
                split[0] + "//" + split[2] + url
            } else if (url.startsWith(".") && (mURL.contains("url=") || mURL.contains("v="))) {
                val split = mURL.split("=".toRegex()).toTypedArray()
                val i = split[0].lastIndexOf("/")
                split[0].substring(0, i) + url.substring(1)
            } else if (url.startsWith(".")) {
                val i = mURL.lastIndexOf("/")
                mURL.substring(0, i) + url.substring(1)
            } else if (url.startsWith("http")) {
                return url
            } else {
                val split = mURL.split("/".toRegex()).toTypedArray()
                return split[0] + "//" + split[2] + "/" + url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * 執行js 獲取 html
     *
     * @param js
     */
    fun evalScript(view: WebView?, js: String) {
        if (TextUtils.isEmpty(js) || view == null) return
        val newJs =
            "javascript:$js(document.getElementsByTagName('html')[0].innerHTML + '$HTMLFLAG');"
        view.loadUrl(newJs)
    }

    /**
     * 获取 html
     */
    fun getHtmlSource(view: WebView?) {
        if (view == null) return
        //        String newJs = "javascript:(document.getElementsByTagName('html')[0].innerHTML + '" + HTMLFLAG + "');";
        view.loadUrl(
            "javascript:window.anime_html_source.htmlSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');"
        )
    }

    /**
     * 覆盖java默认的证书验证
     */
    private val trustAllCerts =
        arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        })

    /**
     * 设置不验证主机
     */
    private val DO_NOT_VERIFY = HostnameVerifier { hostname, session -> true }

    /**
     * 信任所有
     * @param connection
     * @return
     */
    private fun trustAllHosts(connection: HttpsURLConnection?): SSLSocketFactory {
        val oldFactory = connection!!.sslSocketFactory
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            val newFactory = sc.socketFactory
            connection.sslSocketFactory = newFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return oldFactory
    }
}