package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnimeSendDanmakuBean(
    @SerializedName("author")
    var author: String,
    @SerializedName("color")
    var color: String,
    @SerializedName("player")
    var player: String,
    @SerializedName("referer")
    var referer: String,
    @SerializedName("size")
    var size: String,
    @SerializedName("text")
    var text: String,
    @SerializedName("time")
    var time: Double,
    @SerializedName("type")
    var type: String
) : SendDanmakuBean
