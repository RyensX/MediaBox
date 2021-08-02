package com.skyd.skin.core.attrs

import android.view.View
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.skin.SkinManager


class SrlPrimaryColorAttr : SkinAttr() {
    companion object {
        const val TAG = "srlPrimaryColor"

        // 设置SrlPrimaryColorAttr时，同时设置刷新头和尾的值
        var materialHeaderColorSchemeRes: Int = -1
        var ballPulseFooterAnimatingColorRes: Int = -1
    }

    override fun applySkin(view: View) {
        if (view is SmartRefreshLayout && attrResourceRefId != -1) {
            SkinManager.setSrlPrimaryColorAttr(view, attrResourceRefId)
            view.refreshHeader.let {
                if (materialHeaderColorSchemeRes != -1 && it is MaterialHeader) {
                    SkinManager.setColorSchemeColors(it, materialHeaderColorSchemeRes)
                }
            }
            view.refreshFooter.let {
                if (ballPulseFooterAnimatingColorRes != -1 && it is BallPulseFooter) {
                    SkinManager.setAnimatingColor(it, materialHeaderColorSchemeRes)
                }
            }
        }
    }
}