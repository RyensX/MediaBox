package com.su.mediabox.bean

import com.su.mediabox.pluginapi.been.BaseBean

@Deprecated("等待设计新主题系统")
class SkinBean(
    override var type: String,
    override var actionUrl: String,
    var cover: Any,         // Int颜色，或String图片链接
    var title: String,
    var using: Boolean,      // 正在使用
    var skinPath: String,
    var skinSuffix: String
) : BaseBean
