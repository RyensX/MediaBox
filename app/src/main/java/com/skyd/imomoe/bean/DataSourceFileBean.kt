package com.skyd.imomoe.bean

import com.su.mediabox.plugin.standard.been.BaseBean
import java.io.File

class DataSourceFileBean(
    override var type: String,
    override var actionUrl: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean