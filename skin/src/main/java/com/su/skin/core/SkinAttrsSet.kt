package com.su.skin.core

import com.su.skin.core.attrs.SkinAttr

/**
 * 存储所有的属性集合
 */
class SkinAttrsSet {
    var attrsMap: HashMap<String, SkinAttr> = HashMap()

    override fun toString() = attrsMap.toString()
}