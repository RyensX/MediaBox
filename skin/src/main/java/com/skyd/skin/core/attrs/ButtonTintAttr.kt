package com.skyd.skin.core.attrs

import android.view.View
import android.widget.CompoundButton
import com.skyd.skin.SkinManager

class ButtonTintAttr : SkinAttr() {
    companion object {
        const val TAG = "buttonTint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is CompoundButton)
            SkinManager.setButtonTint(view, attrResourceRefId)
    }
}