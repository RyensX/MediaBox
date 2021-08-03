package com.skyd.skin.core.attrs

import android.view.View
import android.widget.SeekBar
import com.skyd.skin.SkinManager


class ThumbAttr : SkinAttr() {
    companion object {
        const val TAG = "thumb"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is SeekBar) {
            SkinManager.setThumb(view, attrResourceRefId)
        }
    }
}