package com.skyd.imomoe.net

import android.util.Log
import android.util.LruCache
import com.skyd.imomoe.App
import com.skyd.imomoe.bean.DoHJsonBean
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import com.google.gson.Gson
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.currentTimeSecond
import com.skyd.imomoe.util.debug
import okhttp3.HttpUrl
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


object DoH {
    private val doh = LruCache<String, Pair<Long, DoHJsonBean.Answer>>(12)
    private var useDoH: Boolean? = null

    init {
        useDoh()
    }

    /**
     * 接受的字符串为host
     * @param type 1: ipv4  28: ipv6
     * @param autoTry 若匹配不到合适的协议，是否自动尝试另一个
     */
    fun String.toIPAddress(type: Int = 28, autoTry: Boolean = true): String {
        // 双重校验锁，避免在没有缓存的情况下短时间内多次请求相同的内容
        val ipData = doh[this]
        if (ipData != null && currentTimeSecond() - ipData.first < ipData.second.ttl) {
            debug {
                val now = currentTimeSecond()
                Log.d(
                    "DoH LruCache",
                    "hit: $this -> ${ipData.second.data} (${ipData.second.ttl - now + ipData.first}s remaining)"
                )
            }
            return if (ipData.second.type == 28) "[${ipData.second.data}]"
            else ipData.second.data
        } else {
            synchronized(this@DoH) {
                doh[this].let {
                    if (it != null && currentTimeSecond() - it.first < it.second.ttl) {
                        debug {
                            val now = currentTimeSecond()
                            Log.d(
                                "DoH LruCache",
                                "hit: $this -> ${it.second.data} (${it.second.ttl - now + it.first}s remaining)"
                            )
                        }
                        return if (it.second.type == 28) "[${it.second.data}]"
                        else it.second.data
                    }
                }
                try {
                    var connection: HttpURLConnection? = null
                    try {
                        val url = URL("${Api.DOH_URL}?name=${this}&type=${type}")
                        connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.setRequestProperty("Accept", "application/dns-json")
                        connection.setRequestProperty("Host", URL(Api.DOH_URL).host)
                        connection.connect()
                        val answers = Gson().fromJson(
                            InputStreamReader(connection.inputStream),
                            DoHJsonBean::class.java
                        ).answer
                        val answer = answers!!.last()
                        return if (type == answer.type) {
                            debug {
                                if (doh[this] == null) {
                                    Log.d("DoH LruCache", "miss: $this -> ${answer.data}")
                                } else {
                                    Log.d("DoH LruCache", "ttl timeout: $this -> ${answer.data}")
                                }
                            }
                            doh.put(this, Pair(System.currentTimeMillis() / 1000, answer))
                            if (answer.type == 28) "[${answer.data}]"
                            else answer.data
                        } else {
                            if (!autoTry) this
                            else if (type == 1) toIPAddress(28, false)
                            else if (type == 28) toIPAddress(1, false)
                            else this
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        connection?.disconnect()
                    }
                } catch (e: NullPointerException) {
                    return this
                }
            }
        }
        return this
    }

    fun useDoh(): Boolean =
        useDoH ?: App.context.sharedPreferences().getBoolean("useDoH", false)
            .apply { useDoH = this }

    fun useDoh(use: Boolean) {
        if (useDoH == use) return
        App.context.sharedPreferences().editor { putBoolean("useDoH", use) }
        useDoH = use
    }

    val doHInterceptor: Interceptor by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DoHInterceptor() }

    private class DoHInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (!useDoh()) return chain.proceed(request)
            val builder = request.newBuilder()
            val oldHttpUrl: HttpUrl = request.url
            val newFullUrl: HttpUrl = oldHttpUrl
                .newBuilder()
                .host(oldHttpUrl.host.toIPAddress())
                .build()
//            debug { Log.d("DoH", "$oldHttpUrl --> $newFullUrl}") }
            return chain.proceed(builder.url(newFullUrl).addHeader("Host", oldHttpUrl.host).build())
        }
    }
}