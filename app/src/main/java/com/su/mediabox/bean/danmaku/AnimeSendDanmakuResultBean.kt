package com.su.mediabox.bean.danmaku

import com.google.gson.annotations.SerializedName

class AnimeSendDanmakuResultBean(
    @SerializedName("code")
    var code: Long,
    @SerializedName("msg")
    var message: String
)