package com.skyd.imomoe.bean

class LicenseBean(
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String,
    var license: String
) : BaseBean