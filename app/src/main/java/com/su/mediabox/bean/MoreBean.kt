package com.su.mediabox.bean

import androidx.annotation.DrawableRes
import com.su.mediabox.pluginapi.been.BaseBean

class MoreBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean
