package com.skyd.imomoe.util.dlna

import android.content.Context
import android.net.wifi.WifiManager
import android.text.TextUtils
import java.util.*

object Utils {
    //TODO:check auth or multiple ip
    @JvmStatic
    fun getWiFiIPAddress(context: Context): String {
        val wifiManager = getSystemService<WifiManager>(context, Context.WIFI_SERVICE)
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo != null) {
            val address = wifiInfo.ipAddress
            (address and 0xFF).toString() + "." + (address shr 8 and 0xFF) + "." + (address shr 16 and 0xFF) + "." + (address shr 24 and 0xFF)
        } else "unknown"
    }

    private fun <T : Any?> getSystemService(
        context: Context,
        name: String
    ): T {
        return context.applicationContext.getSystemService(name) as T
    }

    /**
     * 把时间戳转换成 00:00:00 格式
     *
     * @param timeMs 时间戳
     * @return 00:00:00 时间格式
     */
    fun getStringTime(timeMs: Long): String {
        val formatBuilder = StringBuilder()
        val formatter = Formatter(formatBuilder, Locale.US)
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    /**
     * 把 00:00:00 格式转成时间戳
     *
     * @param formatTime 00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    fun getIntTime(formatTime: String): Long {
        if (!TextUtils.isEmpty(formatTime)) {
            val tmp = formatTime.split(":".toRegex()).toTypedArray()
            if (tmp.size < 3) return 0
            val second = tmp[0].toInt() * 3600 + tmp[1].toInt() * 60 + tmp[2].toInt()
            return second * 1000L
        }
        return 0
    }
}