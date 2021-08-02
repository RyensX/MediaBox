package com.skyd.skin.core.attrs

import android.view.View
import android.widget.ImageView
import com.skyd.skin.SkinManager


class SrcAttr : SkinAttr() {
    companion object {
        const val TAG = "src"
    }

    override fun applySkin(view: View) {
        if (view is ImageView && attrResourceRefId != -1) {
            SkinManager.setSrc(view, attrResourceRefId)
        }
    }
}