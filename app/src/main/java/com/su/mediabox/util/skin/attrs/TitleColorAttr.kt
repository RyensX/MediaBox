package com.su.mediabox.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.su.mediabox.view.component.AnimeToolbar
import com.su.skin.core.SkinResourceProcessor
import com.su.skin.core.attrs.SkinAttr


class TitleColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is AnimeToolbar && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setTitleColor(ContextCompat.getColorStateList(view.context, attrResourceRefId))
            } else {
                view.setTitleColor(skinResProcessor.getColorStateList(attrResourceRefId))
            }
        }
    }

    override fun tag(): String = "titleColor"
}