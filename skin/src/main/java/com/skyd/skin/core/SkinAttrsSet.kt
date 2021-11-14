package com.skyd.skin.core

import com.skyd.skin.core.attrs.SkinAttr

/**
 * 存储所有的属性集合
 */
class SkinAttrsSet {
    var attrsMap: HashMap<String, SkinAttr> = HashMap()

    override fun toString() = attrsMap.toString()
}