package com.skyd.skin.core.attrs

import android.view.View
import com.skyd.skin.SkinManager

class BackgroundTintAttr : SkinAttr() {
    companion object {
        const val TAG = "backgroundTint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) SkinManager.setBackgroundTint(view, attrResourceRefId)
    }
}