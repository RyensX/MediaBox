package com.skyd.skin.core.attrs

import android.view.View
import com.skyd.skin.SkinManager


class BackgroundAttr : SkinAttr() {
    companion object {
        const val TAG = "background"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) SkinManager.setBackground(view, attrResourceRefId)
    }
}