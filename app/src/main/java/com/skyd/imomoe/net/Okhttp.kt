package com.skyd.imomoe.net

import com.skyd.imomoe.util.coil.CoilUtil
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.io.File
import java.net.InetAddress

private val okhttpCache = Cache(File("cacheDir", "okhttpcache"), 10 * 1024 * 1024)
private val bootstrapClient = OkHttpClient.Builder().cache(okhttpCache).build()

var dns: DnsOverHttps? = DoH.dnsServer.let {
    if (it.isNullOrBlank()) null else {
        DnsOverHttps.Builder().client(bootstrapClient)
            .url(it.toHttpUrl())
//            .bootstrapDnsHosts(InetAddress.getByName("1.0.0.1"))
            .build()
    }
}

var okhttpClient = bootstrapClient.newBuilder().apply { dns?.let { dns(it) } }.build()

fun changeDnsServer(server: String) {
    dns = if (server.isBlank()) null else {
        DnsOverHttps.Builder().client(bootstrapClient)
            .url(server.toHttpUrl())
//            .bootstrapDnsHosts(InetAddress.getByName("1.0.0.1"))
            .build()
    }
    okhttpClient = bootstrapClient.newBuilder().apply { dns?.let { dns(it) } }.build()
    RetrofitManager.get().client(okhttpClient)
    CoilUtil.setOkHttpClient(okhttpClient)
}
