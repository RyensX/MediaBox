package com.su.mediabox.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.su.mediabox.view.component.bannerview.indicator.DotIndicator
import com.su.skin.core.SkinResourceProcessor
import com.su.skin.core.attrs.SkinAttr


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