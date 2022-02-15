package com.su.mediabox.bean

import com.su.mediabox.pluginapi.been.BaseBean

class LicenseBean(
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String,
    var license: String
) : BaseBean
