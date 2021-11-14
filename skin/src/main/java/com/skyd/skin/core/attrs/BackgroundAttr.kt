package com.skyd.skin.core.attrs

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class BackgroundAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.background = ContextCompat.getDrawable(view.context, attrResourceRefId)
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                if (skinResourceId is Int) {
                    view.setBackgroundColor(skinResourceId)
                } else {
                    view.background = skinResourceId as Drawable
                }
            }
        }
    }

    override fun tag(): String = "background"
}