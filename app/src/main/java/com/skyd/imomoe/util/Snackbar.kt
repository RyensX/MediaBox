package com.skyd.imomoe.util

import android.app.Activity
import android.content.res.ColorStateList
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.getResColor

fun CharSequence.showSnackbar(
    activity: Activity,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = App.context.getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.main_color_2_skin)),
    textColor: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.foreground_white_skin)),
    actionTextColor: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.foreground_white_skin))
) {
    showSnackbar(
        view = activity.findViewById(android.R.id.content),
        duration = duration,
        actionText = actionText,
        actionCallback = actionCallback,
        backgroundTintList = backgroundTintList,
        textColor = textColor,
        actionTextColor = actionTextColor
    )
}


fun CharSequence.showSnackbar(
    view: View,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = App.context.getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.main_color_2_skin)),
    textColor: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.foreground_white_skin)),
    actionTextColor: ColorStateList? = ColorStateList.valueOf(getResColor(R.color.foreground_white_skin))
) {
    Snackbar.make(view, this, duration)
        .setAction(actionText) { actionCallback?.invoke() }
        .apply {
            if (backgroundTintList != null) setBackgroundTintList(backgroundTintList)
            if (textColor != null) setTextColor(textColor)
            if (actionTextColor != null) setActionTextColor(textColor)
        }
        .show()
}
