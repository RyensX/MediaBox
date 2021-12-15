package com.skyd.imomoe.util.skin.attrs

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.imomoe.view.component.AnimeToolbar
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.SkinAttr


class ToolBarBackgroundAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is AnimeToolbar && attrResourceRefId != -1) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setToolbarBackground(
                    ContextCompat.getDrawable(view.context, attrResourceRefId)
                )
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                if (skinResourceId is Int) {
                    view.setToolbarBackgroundColor(skinResourceId)
                } else {
                    view.setToolbarBackground(skinResourceId as Drawable)
                }
            }
        }
    }

    override fun tag(): String = "toolBarBackground"
}
