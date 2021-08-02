package com.skyd.skin.core.attrs

import android.view.View
import android.widget.ImageView
import com.skyd.skin.SkinManager


class ImageViewTintAttr : SkinAttr() {
    companion object {
        const val TAG = "imageViewTint"
    }

    override fun applySkin(view: View) {
        if (view is ImageView && attrResourceRefId != -1) {
            SkinManager.setImageViewTint(view, attrResourceRefId)
        }
    }
}