package com.skyd.skin.core.attrs

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class TextColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is TextView && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setTextColor(ContextCompat.getColorStateList(view.context, attrResourceRefId))
            } else {
                view.setTextColor(skinResProcessor.getColorStateList(attrResourceRefId))
            }
        }
    }

    override fun tag(): String = "textColor"
}