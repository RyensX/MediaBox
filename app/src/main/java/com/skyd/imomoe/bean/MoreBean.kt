package com.skyd.imomoe.bean

import androidx.annotation.DrawableRes

class MoreBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean
