package com.su.mediabox.bean

import androidx.annotation.DrawableRes
import com.su.mediabox.pluginapi.been.BaseBean

@Deprecated("不需要继承BaseBeen多那么多冗余字段，也不应该持久化视图类型")
class MoreBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean
