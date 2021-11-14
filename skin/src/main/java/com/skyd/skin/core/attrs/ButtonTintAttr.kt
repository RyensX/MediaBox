package com.skyd.skin.core.attrs

import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor

class ButtonTintAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is CompoundButton) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.buttonTintList =
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
            } else {
                view.buttonTintList = skinResProcessor.getColorStateList(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "buttonTint"
}