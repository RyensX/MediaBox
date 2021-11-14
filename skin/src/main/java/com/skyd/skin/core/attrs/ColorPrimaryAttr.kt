package com.skyd.skin.core.attrs

import android.view.View
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skyd.skin.core.SkinResourceProcessor


class ColorPrimaryAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId == -1) return
        // 按需添加
        when (view) {
            is SwipeRefreshLayout -> {
                val skinResProcessor = SkinResourceProcessor.instance
                if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                    view.setColorSchemeColors(
                        ContextCompat.getColor(view.context, attrResourceRefId)
                    )
                } else {
                    view.setColorSchemeColors(skinResProcessor.getColor(attrResourceRefId))
                }
            }
        }
    }

    override fun tag(): String = "colorPrimary"
}