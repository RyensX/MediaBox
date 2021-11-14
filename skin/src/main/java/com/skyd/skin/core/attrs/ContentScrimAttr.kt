package com.skyd.skin.core.attrs

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.skyd.skin.core.SkinResourceProcessor


class ContentScrimAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is CollapsingToolbarLayout && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.contentScrim = ContextCompat.getDrawable(view.context, attrResourceRefId)
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                if (skinResourceId is Int) {
                    view.setContentScrimColor(skinResourceId)
                } else {
                    view.contentScrim = skinResourceId as Drawable
                }
            }
        }
    }

    override fun tag(): String = "contentScrim"
}