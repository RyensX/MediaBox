package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import com.skyd.imomoe.bean.BaseBean

class AnimeSendDanmakuResultBean(
    override var type: String,
    override var actionUrl: String,
    @SerializedName("code")
    var code: Long,
    @SerializedName("msg")
    var message: String
) : BaseBean
