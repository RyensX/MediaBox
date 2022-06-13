package com.su.mediabox.util

import android.util.Log
import com.su.mediabox.BuildConfig

/**
 * 全局Log输出开关
 */
var globalLogPrintEnable = true

fun logV(tag: String, msg: String, releasePrint: Boolean = true) {
    if (globalLogPrintEnable && (releasePrint || BuildConfig.DEBUG))
        Log.v(tag, msg)
}

fun logD(tag: String, msg: String, releasePrint: Boolean = true) {
    if (globalLogPrintEnable && (releasePrint || BuildConfig.DEBUG))
        Log.d(tag, msg)
}

fun logI(tag: String, msg: String, releasePrint: Boolean = true) {
    if (globalLogPrintEnable && (releasePrint || BuildConfig.DEBUG))
        Log.i(tag, msg)
}

fun logW(tag: String, msg: String, releasePrint: Boolean = true) {
    if (globalLogPrintEnable && (releasePrint || BuildConfig.DEBUG))
        Log.w(tag, msg)
}

fun logE(tag: String, msg: String, releasePrint: Boolean = true) {
    if (globalLogPrintEnable && (releasePrint || BuildConfig.DEBUG))
        Log.e(tag, msg)
}
