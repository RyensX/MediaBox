package com.skyd.imomoe.bean

class TabBean(
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean
