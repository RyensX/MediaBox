package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DoHJsonBean(
    override var type: String = "",
    override var actionUrl: String = "",
    @SerializedName("Status")
    var status: Int,
    @SerializedName("TC")
    var tc: Boolean,
    @SerializedName("RD")
    var rd: Boolean,
    @SerializedName("RA")
    var ra: Boolean,
    @SerializedName("AD")
    var ad: Boolean,
    @SerializedName("CD")
    var cd: Boolean,
    @SerializedName("Question")
    var question: List<Question>,
    @SerializedName("Answer")
    var answer: List<Answer>?,
    @SerializedName("Comment")
    var comment: String?,
    @SerializedName("edns_client_subnet")
    var eDNSClientSubnet: String?
) : BaseBean {
    class Question(
        @SerializedName("name")
        var name: String,
        @SerializedName("type")
        var type: Int
    ) : Serializable

    class Answer(
        @SerializedName("name")
        var name: String,
        @SerializedName("type")
        var type: Int,
        @SerializedName("TTL")
        var ttl: Int,
        @SerializedName("data")
        var data: String
    ) : Serializable
}
