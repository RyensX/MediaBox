package com.skyd.imomoe.bean

import java.io.File

class DataSourceFileBean(
    override var type: String,
    override var actionUrl: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean