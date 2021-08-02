package com.skyd.skin.core.attrs

import android.view.View
import androidx.cardview.widget.CardView
import com.skyd.skin.SkinManager

class CardBackgroundColorAttr : SkinAttr() {
    companion object {
        const val TAG = "cardBackgroundColor"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is CardView)
            SkinManager.setCardBackgroundColor(view, attrResourceRefId)
    }
}