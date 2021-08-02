package com.skyd.skin.core.attrs

import android.view.View
import android.widget.TextView
import com.skyd.skin.SkinManager

class TextColorHintAttr : SkinAttr() {
    companion object {
        const val TAG = "textColorHint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is TextView)
            SkinManager.setTextColorHint(view, attrResourceRefId)
    }
}