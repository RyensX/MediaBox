package com.skyd.imomoe.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.imomoe.view.component.bannerview.indicator.DotIndicator
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.SkinAttr


class BannerIndicatorDotSelectedColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is DotIndicator) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.mSelectedColor = ContextCompat.getColor(view.context, attrResourceRefId)
            } else {
                view.mSelectedColor = skinResProcessor.getColor(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "bannerIndicatorDotSelectedColorAttr"
}