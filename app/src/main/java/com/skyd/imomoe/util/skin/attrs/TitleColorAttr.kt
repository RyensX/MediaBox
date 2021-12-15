package com.skyd.imomoe.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.imomoe.view.component.AnimeToolbar
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.SkinAttr


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