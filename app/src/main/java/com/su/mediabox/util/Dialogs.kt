package com.su.mediabox.util

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.internal.button.DialogActionButton
import com.su.mediabox.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


fun MaterialDialog.countdownActionButton(
    which: WhichButton = WhichButton.POSITIVE,
    durationSeconds: Int = 10
) = getActionButton(which).countdownActionButton(durationSeconds)

@SuppressLint("SetTextI18n")
fun DialogActionButton.countdownActionButton(
    durationSeconds: Int = 10
) = also {
    val rawText = it.text
    it.isEnabled = false
    appCoroutineScope.launch(Dispatchers.Main) {
        var s = durationSeconds
        while (s > 0) {
            it.text = "$rawText($s)"
            delay(1000)
            s--
        }
        it.text = rawText
        it.isEnabled = true
    }
}

suspend fun getTextDialog(context: Context, title: String): String? {
    return suspendCancellableCoroutine {
        val et = EditText(context)
        AlertDialog.Builder(context)
            .setTitle(title)
            .setIcon(R.drawable.ic_dialog_info).setView(et)
            .setPositiveButton("确定") { _, _ ->
                it.resume(et.text.toString())
            }.setNegativeButton("取消") { _, _ ->
                it.resume(null)
            }.show()
    }
}