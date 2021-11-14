package com.skyd.skin.core.attrs

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor

class TextColorHintAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is TextView) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setHintTextColor(
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
                )
            } else {
                view.setHintTextColor(skinResProcessor.getColorStateList(attrResourceRefId))
            }
        }
    }

    override fun tag(): String = "textColorHint"
}