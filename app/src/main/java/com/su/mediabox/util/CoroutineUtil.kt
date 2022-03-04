package com.su.mediabox.util

import android.app.Dialog
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