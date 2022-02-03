package com.skyd.imomoe.bean

import androidx.annotation.DrawableRes
import com.su.mediabox.plugin.standard.been.BaseBean

class MoreBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean
