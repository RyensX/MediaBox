package com.skyd.skin.core.attrs

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor

class TrackTintAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is SwitchCompat) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.trackTintList =
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
            } else {
                view.trackTintList = skinResProcessor.getColorStateList(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "trackTint"
}