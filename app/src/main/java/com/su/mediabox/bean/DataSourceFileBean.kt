package com.su.mediabox.bean

import com.su.mediabox.pluginapi.been.BaseBean
import java.io.File

class DataSourceFileBean(
    override var type: String,
    override var actionUrl: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean