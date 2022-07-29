package com.su.mediabox.util

import android.annotation.SuppressLint
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.internal.button.DialogActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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