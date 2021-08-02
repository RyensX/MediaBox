package com.skyd.skin.core.attrs

import android.view.View
import com.skyd.skin.SkinManager
import com.google.android.material.tabs.TabLayout


class TabIndicatorColorAttr : SkinAttr() {
    companion object {
        const val TAG = "tabIndicatorColor"
    }

    override fun applySkin(view: View) {
        if (view is TabLayout && attrResourceRefId != -1) {
            SkinManager.setTabIndicatorColor(view, attrResourceRefId)
        }
    }
}