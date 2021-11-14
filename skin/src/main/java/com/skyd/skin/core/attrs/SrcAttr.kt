package com.skyd.skin.core.attrs

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class SrcAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is ImageView && attrResourceRefId != -1) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                // 兼容包转换
                view.setImageResource(attrResourceRefId)
                view.setImageDrawable(ContextCompat.getDrawable(view.context, attrResourceRefId))
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(attrResourceRefId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setImageResource(skinResourceId)
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    view.setImageDrawable(skinResourceId as Drawable)
                }
            }
        }
    }

    override fun tag(): String = "src"
}