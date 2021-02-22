package com.skyd.imomoe.util

import android.content.Context
import android.content.SharedPreferences

fun Context.sharedPreferences(name: String): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)
fun SharedPreferences.editor(editorBuilder: SharedPreferences.Editor.() -> Unit) = edit().apply(editorBuilder).apply()