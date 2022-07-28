package com.su.mediabox.util

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.su.mediabox.App

object ResourceUtil {

    fun getDrawable(@DrawableRes id: Int, @ColorRes tintColor: Int? = null): Drawable? {
        val icon = AppCompatResources
            .getDrawable(App.context, id) ?: return null
        tintColor?.also {
            DrawableCompat.setTint(icon, ContextCompat.getColor(App.context, tintColor))
        }
        return icon
    }

    fun getString(@StringRes id: Int, vararg args: Any) = App.context.getString(id, *args)
}