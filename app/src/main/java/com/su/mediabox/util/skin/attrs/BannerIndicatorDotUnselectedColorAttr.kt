package com.su.mediabox.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.su.mediabox.view.component.bannerview.indicator.DotIndicator
import com.su.skin.core.SkinResourceProcessor
import com.su.skin.core.attrs.SkinAttr


class BannerIndicatorDotUnselectedColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is DotIndicator) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.mUnSelectedColor = ContextCompat.getColor(view.context, attrResourceRefId)
            } else {
                view.mUnSelectedColor = skinResProcessor.getColor(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "bannerIndicatorDotUnselectedColorAttr"
}