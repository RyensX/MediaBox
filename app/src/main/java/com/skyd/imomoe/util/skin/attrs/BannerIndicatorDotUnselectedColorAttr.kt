package com.skyd.imomoe.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.imomoe.view.component.bannerview.indicator.DotIndicator
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.SkinAttr


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