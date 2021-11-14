package com.skyd.skin.core.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor

class BackgroundTintAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.backgroundTintList =
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
            } else {
                view.backgroundTintList = skinResProcessor.getColorStateList(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "backgroundTint"
}