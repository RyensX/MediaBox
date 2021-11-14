package com.skyd.skin.core.attrs

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor


class ImageViewTintAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is ImageView && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.imageTintList =
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
            } else {
                view.imageTintList = skinResProcessor.getColorStateList(attrResourceRefId)
            }
        }
    }

    override fun tag(): String = "imageViewTint"
}