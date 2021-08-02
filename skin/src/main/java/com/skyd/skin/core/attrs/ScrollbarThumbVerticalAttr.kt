package com.skyd.skin.core.attrs

import android.view.View
import com.skyd.skin.SkinManager


class ScrollbarThumbVerticalAttr : SkinAttr() {
    companion object {
        const val TAG = "scrollbarThumbVertical"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) SkinManager.setScrollbarThumbVertical(view, attrResourceRefId)
    }
}