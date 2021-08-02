package com.skyd.skin.core.attrs

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.skyd.skin.SkinManager

class ThumbTintAttr : SkinAttr() {
    companion object {
        const val TAG = "thumbTint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is SwitchCompat)
            SkinManager.setThumbTint(view, attrResourceRefId)
    }
}