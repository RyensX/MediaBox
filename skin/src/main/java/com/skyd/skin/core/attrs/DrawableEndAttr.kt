package com.skyd.skin.core.attrs

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class DrawableEndAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is TextView && attrResourceRefId != -1) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null, ContextCompat.getDrawable(view.context, attrResourceRefId), null
                )
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null, ContextCompat.getDrawable(view.context, attrResourceRefId), null
                    )
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    val drawable = skinResourceId as Drawable
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
                }
            }
        }
    }

    override fun tag(): String = "drawableEnd"
}