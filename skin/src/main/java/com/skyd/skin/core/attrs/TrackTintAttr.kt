package com.skyd.skin.core.attrs

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.skyd.skin.SkinManager

class TrackTintAttr : SkinAttr() {
    companion object {
        const val TAG = "trackTint"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId != -1 && view is SwitchCompat)
            SkinManager.setTrackTint(view, attrResourceRefId)
    }
}