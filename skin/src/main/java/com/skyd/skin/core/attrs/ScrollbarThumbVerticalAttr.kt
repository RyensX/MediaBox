package com.skyd.skin.core.attrs

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class ScrollbarThumbVerticalAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 是否默认皮肤
                val skinResProcessor = SkinResourceProcessor.instance
                if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                    view.verticalScrollbarThumbDrawable =
                        ContextCompat.getDrawable(view.context, attrResourceRefId)
                } else {
                    // 获取皮肤包资源
                    val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                    if (skinResourceId is Int) {
                        view.verticalScrollbarThumbDrawable = ColorDrawable(skinResourceId)
                    } else {
                        view.verticalScrollbarThumbDrawable = skinResourceId as Drawable
                    }
                }
            }
        }
    }

    override fun tag(): String = "scrollbarThumbVertical"
}