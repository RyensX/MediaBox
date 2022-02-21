package com.su.skin.core.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.su.skin.core.SkinResourceProcessor


class TabTextColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is TabLayout && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.tabTextColors =
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
            } else {
                view.tabTextColors = skinResProcessor.getColorStateList(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "tabTextColor"
}