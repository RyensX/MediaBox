package com.skyd.skin.core.attrs

import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.skyd.skin.core.SkinResourceProcessor

class CardBackgroundColorAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is CardView) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setCardBackgroundColor(
                    ContextCompat.getColorStateList(view.context, attrResourceRefId)
                )
            } else {
                view.setCardBackgroundColor(skinResProcessor.getColorStateList(attrResourceRefId))
            }
        }
    }

    override fun tag(): String = "cardBackgroundColor"
}