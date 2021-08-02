package com.skyd.skin.core.attrs

import android.view.View
import android.widget.ProgressBar
import com.skyd.skin.SkinManager

class IndeterminateTintAttr : SkinAttr() {
    companion object {
        const val TAG = "indeterminateTint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is ProgressBar)
            SkinManager.setIndeterminateTint(view, attrResourceRefId)
    }
}