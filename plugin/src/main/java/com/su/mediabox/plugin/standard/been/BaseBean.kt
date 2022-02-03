package com.su.mediabox.plugin.standard.been

import java.io.Serializable

interface BaseBean : Serializable {
    var type: String
    var actionUrl: String
}