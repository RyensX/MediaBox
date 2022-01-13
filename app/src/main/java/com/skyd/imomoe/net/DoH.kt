package com.skyd.imomoe.net

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences
import com.skyd.imomoe.util.showToast
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.lang.Exception

object DoH {
    val defaultDnsServer = hashMapOf(
        0 to "",
        1 to "https://223.5.5.5/dns-query",
        2 to "https://1.0.0.1/dns-query",
        3 to "https://8.8.8.8/dns-query"
    )

    val defaultDnsServerName = hashMapOf(
        0 to "不使用",
        1 to "alidns(223.5.5.5)",
        2 to "Cloudflare(1.0.0.1)",
        3 to "Google(8.8.8.8)"
    )

    var dnsServer: String? = null
        set(value) {
            if (value == null || value == field) return
            App.context.sharedPreferences().editor { putString("dnsServer", value) }
            field = value
            changeDnsServer(value)
        }
        get() {
            return field ?: App.context.sharedPreferences()
                .getString("dnsServer", null).apply { field = this }
        }

    fun AppCompatActivity.selectDnsServer() {
        var initialSelection = -1
        defaultDnsServer.values.forEachIndexed { index, s ->
            if (s == dnsServer) initialSelection = index
        }
        if (dnsServer.isNullOrBlank()) initialSelection = 0
        MaterialDialog(this).listItemsSingleChoice(
            items = defaultDnsServerName.values.toList(),
            initialSelection = initialSelection
        ) { _, index, _ ->
            dnsServer = defaultDnsServer[index]
        }.positiveButton(R.string.ok).negativeButton(R.string.custom_dns_server) {
            customDnsServer()
            it.dismiss()
        }.show {
            title(res = R.string.select_dns_server)
        }
    }

    fun AppCompatActivity.customDnsServer() {
        MaterialDialog(this).input(hintRes = R.string.custom_dns_server_describe) { _, text ->
            val url = text.toString()
            try {
                // 测试url合法性
                url.toHttpUrl()
                dnsServer = url
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.showToast()
            }
        }.show()
    }
}