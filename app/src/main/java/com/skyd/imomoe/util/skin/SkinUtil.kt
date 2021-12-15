package com.skyd.imomoe.util.skin

import com.skyd.imomoe.R
import com.skyd.imomoe.util.skin.attrs.TitleColorAttr
import com.skyd.imomoe.util.skin.attrs.ToolBarBackgroundAttr
import com.skyd.skin.SkinManager
import com.skyd.skin.core.attrs.SkinAttr


object SkinUtil {
    fun initCustomAttrIds() {
        SkinManager.addCustomAttrId(R.attr.titleColor,
            object : SkinManager.CustomSetSkinTagListener {
                override fun setSkinTag(attrId: Int, resId: Int): Pair<String, SkinAttr>? {
                    if (resId != -1) return TitleColorAttr().run {
                        attrResourceRefId = resId
                        Pair(tag(), this)
                    }
                    return null
                }
            })
        SkinManager.addCustomAttrId(R.attr.toolBarBackground,
            object : SkinManager.CustomSetSkinTagListener {
                override fun setSkinTag(attrId: Int, resId: Int): Pair<String, SkinAttr>? {
                    if (resId != -1) return ToolBarBackgroundAttr().run {
                        attrResourceRefId = resId
                        Pair(tag(), this)
                    }
                    return null
                }
            })
    }
}
