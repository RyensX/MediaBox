package com.su.mediabox.util

import android.app.Dialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * 创建一个符合Dialog生命周期的协程域，会占用[Dialog.setOnCancelListener]
 */
fun Dialog.createCoroutineScope(context: CoroutineContext = Dispatchers.Default) =
    CoroutineScope(context).also { cs ->
        setOnCancelListener {
            cs.cancel()
        }
    }

val pluginExceptionHandler = CoroutineExceptionHandler { _, e ->
    e.printStackTrace()
    when (e.javaClass) {
        NoSuchMethodError::class.java, InstantiationError::class.java -> "该插件API版本过低！请更新插件！".showToast()
        else -> e.message?.showToast()
    }
}

private val pluginIO = Dispatchers.IO + pluginExceptionHandler
val Dispatchers.PluginIO
    get() = pluginIO