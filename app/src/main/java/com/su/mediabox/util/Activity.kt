package com.su.mediabox.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.su.mediabox.pluginapi.action.Action
import com.su.mediabox.util.Util.withoutExceptionGet

inline fun <reified T : Activity> Context.goActivity(intent: Intent = Intent()) {
    intent.setClass(this, T::class.java)
    startActivity(intent)
}

fun <T : Action> Intent.putAction(action: T) = putExtra(action.javaClass.simpleName, action)

inline fun <reified T : Action> Intent.getActionIns(): T? =
    withoutExceptionGet { getSerializableExtra(T::class.java.simpleName) as? T }

inline fun <reified T : Action> Activity.getAction(): T? = intent.getActionIns()

fun <VB : ViewBinding> Activity.viewBind(inflater: (LayoutInflater) -> VB) =
    lazy(LazyThreadSafetyMode.NONE) {
        inflater(layoutInflater).apply {
            setContentView(root)
        }
    }