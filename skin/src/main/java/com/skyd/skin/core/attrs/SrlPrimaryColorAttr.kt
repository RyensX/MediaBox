package com.skyd.skin.core.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.skin.SkinManager
import com.skyd.skin.core.SkinResourceProcessor


class SrlPrimaryColorAttr : SkinAttr() {
    companion object {
        // 设置SrlPrimaryColorAttr时，同时设置刷新头和尾的值
        var materialHeaderColorSchemeRes: Int = -1
        var ballPulseFooterAnimatingColorRes: Int = -1
    }

    override fun applySkin(view: View) {
        if (view is SmartRefreshLayout && attrResourceRefId != -1) {
            SkinManager.setSrlPrimaryColorAttr(view, attrResourceRefId)
            val skinResProcessor = SkinResourceProcessor.instance
            view.refreshHeader.let { header ->
                if (materialHeaderColorSchemeRes != -1 && header is MaterialHeader) {
                    if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                        header.setColorSchemeColors(
                            ContextCompat.getColor(header.context, materialHeaderColorSchemeRes)
                        )
                    } else {
                        header.setColorSchemeColors(
                            skinResProcessor.getColor(materialHeaderColorSchemeRes)
                        )
                    }
                }
            }
            view.refreshFooter.let { footer ->
                if (ballPulseFooterAnimatingColorRes != -1 && footer is BallPulseFooter) {
                    if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                        footer.setAnimatingColor(
                            ContextCompat.getColor(footer.context, materialHeaderColorSchemeRes)
                        )
                    } else {
                        footer.setAnimatingColor(
                            skinResProcessor.getColor(materialHeaderColorSchemeRes)
                        )
                    }
                }
            }
        }
    }

    override fun tag(): String = "srlPrimaryColor"
}