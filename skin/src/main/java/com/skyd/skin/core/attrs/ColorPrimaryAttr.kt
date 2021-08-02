package com.skyd.skin.core.attrs

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skyd.skin.SkinManager


class ColorPrimaryAttr : SkinAttr() {
    companion object {
        const val TAG = "colorPrimary"
    }

    override fun applySkin(view: View) {
        if (attrResourceRefId == -1) return
        // 按需添加
        when (view) {
            is SwipeRefreshLayout -> {
                SkinManager.setColorSchemeColors(view, attrResourceRefId)
            }
        }
    }
}