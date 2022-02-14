package com.su.mediabox.util

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Context.goActivity(intent: Intent = Intent()) {
    intent.setClass(this, T::class.java)
    startActivity(intent)
}