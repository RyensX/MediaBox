package com.skyd.skin.core.attrs

import android.view.View
import android.widget.TextView
import com.skyd.skin.SkinManager


class DrawableTopCompatAttr : SkinAttr() {
    companion object {
        const val TAG = "drawableTopCompat"
    }

    override fun applySkin(view: View) {
        if (view is TextView && attrResourceRefId != -1) {
            SkinManager.setDrawableTopCompat(view, attrResourceRefId)
        }
    }
}