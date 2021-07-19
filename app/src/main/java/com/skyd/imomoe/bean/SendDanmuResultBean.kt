package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName

class SendDanmuResultBean(
    override var type: String,
    override var actionUrl: String,
    @SerializedName("code")
    var code: Long,
    @SerializedName("msg")
    var message: String
) : BaseBean
