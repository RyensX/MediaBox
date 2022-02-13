package com.su.mediabox.util.skin

import com.su.mediabox.R
import com.su.mediabox.util.skin.attrs.TitleColorAttr
import com.su.mediabox.util.skin.attrs.ToolBarBackgroundAttr
import com.su.skin.SkinManager
import com.su.skin.core.attrs.SkinAttr


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
