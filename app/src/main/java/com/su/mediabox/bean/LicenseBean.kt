package com.su.mediabox.bean

import com.su.mediabox.pluginapi.been.BaseBean

@Deprecated("不需要继承BaseBeen多那么多冗余字段，也不应该持久化视图类型")
class LicenseBean(
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String,
    var license: String
) : BaseBean
