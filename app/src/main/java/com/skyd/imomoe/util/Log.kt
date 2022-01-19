package com.skyd.imomoe.util

import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import com.skyd.imomoe.App
import java.text.SimpleDateFormat
import java.util.*
import java.io.*


//class LogManager {
//    companion object {
//        val instance: LogManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { LogManager() }
//
//        const val VERBOSE = "V"
//        const val DEBUG = "D"
//        const val INFO = "I"
//        const val WARN = "W"
//        const val ERROR = "E"
//
//        val CACHE_PATH = App.context.getExternalFilesDir(null).toString() + "/Logs/"
//    }
//
//    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
//
//    private val fileName: String = dateFormat.format(Date(System.currentTimeMillis()))
//
//    private val diskLruCache: DiskLruCache = DiskLruCache.open(File(CACHE_PATH), 1, 1, 7 * 1024)
//
//    fun add(priority: String, tag: String, msg: String) {
//        val editor: DiskLruCache.Editor? = diskLruCache.edit(fileName)
//        if (editor != null) {
//            val outputStream = OutputStreamWriter(editor.newOutputStream(0), Charsets.UTF_8)
//            FileWriter(outputStream)
//            outputStream.write("${dateFormat.format(Date(System.currentTimeMillis()))} ${priority}/${tag}: $msg")
//            editor.commit()
//        }
//        diskLruCache.flush()
//    }
//
//    fun get(): InputStream? {
//        val snapShot: DiskLruCache.Snapshot? = diskLruCache.get(fileName)
//        return snapShot?.getInputStream(0)
//    }
//}

fun logV(tag: String, msg: String) {
    Log.v(tag, msg)
}

fun logD(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun logI(tag: String, msg: String) {
    Log.i(tag, msg)
}

fun logW(tag: String, msg: String) {
    Log.w(tag, msg)
}

fun logE(tag: String, msg: String) {
    Log.e(tag, msg)
}
