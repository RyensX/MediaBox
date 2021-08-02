package com.skyd.skin.core.attrs

import android.view.View
import android.widget.TextView
import com.skyd.skin.SkinManager


class DrawableEndAttr : SkinAttr() {
    companion object {
        const val TAG = "drawableEnd"
    }

    override fun applySkin(view: View) {
        if (view is TextView && attrResourceRefId != -1) {
            SkinManager.setDrawableEnd(view, attrResourceRefId)
        }
    }
}