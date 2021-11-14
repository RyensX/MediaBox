package com.skyd.skin.core.attrs

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class ProgressDrawableAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is ProgressBar) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.progressDrawable = ContextCompat.getDrawable(view.context, attrResourceRefId)
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                if (skinResourceId is Int) {
                    view.progressDrawable = ColorDrawable(skinResourceId)
                } else {
                    view.progressDrawable = skinResourceId as Drawable
                }
            }
        }
    }

    override fun tag(): String = "progressDrawable"
}