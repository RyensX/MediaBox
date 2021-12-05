package com.skyd.imomoe.util

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.skyd.imomoe.App
import java.io.File

val File.uri: Uri
    get() = if (Build.VERSION.SDK_INT >= 24) {
        FileProvider.getUriForFile(App.context, "com.skyd.imomoe.fileProvider", this)
    } else {
        Uri.fromFile(this)
    }

fun String.toFile() = File(this)