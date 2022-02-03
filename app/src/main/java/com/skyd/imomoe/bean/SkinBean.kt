package com.skyd.imomoe.bean

import com.su.mediabox.plugin.standard.been.BaseBean

class SkinBean(
    override var type: String,
    override var actionUrl: String,
    var cover: Any,         // Int颜色，或String图片链接
    var title: String,
    var using: Boolean,      // 正在使用
    var skinPath: String,
    var skinSuffix: String
) : BaseBean
