package com.su.mediabox.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.AttrRes

val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.resolveThemedColor(@AttrRes resId: Int) =
    TypedValue().apply {
        theme.resolveAttribute(resId, this, true)
    }.data

fun Context.uninstallApp(packageName: String) {
    startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")))
}