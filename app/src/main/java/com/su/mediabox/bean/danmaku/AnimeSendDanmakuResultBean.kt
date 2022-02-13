package com.su.mediabox.bean.danmaku

import com.google.gson.annotations.SerializedName
import com.su.mediabox.plugin.standard.been.BaseBean

class AnimeSendDanmakuResultBean(
    override var type: String,
    override var actionUrl: String,
    @SerializedName("code")
    var code: Long,
    @SerializedName("msg")
    var message: String
) : BaseBean
