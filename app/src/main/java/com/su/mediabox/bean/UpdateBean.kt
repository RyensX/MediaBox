package com.su.mediabox.bean

import com.google.gson.annotations.SerializedName
import com.su.mediabox.pluginapi.been.BaseBean

class UpdateBean(
    override var type: String,
    override var actionUrl: String,

    @SerializedName("tag_name")
    var tagName: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("published_at")
    var publishedAt: String,

    @SerializedName("assets")
    var assets: List<AssetsBean>,

    @SerializedName("body")
    var body: String

    ) : BaseBean {

    class AssetsBean(
        override var type: String,
        override var actionUrl: String,

        @SerializedName("name")
        var name: String,

        @SerializedName("size")
        var size: Long,

        @SerializedName("download_count")
        var downloadCount: String?,

        @SerializedName("browser_download_url")
        var browserDownloadUrl: String,

        @SerializedName("created_at")
        var createdAt: String?,

        @SerializedName("updated_at")
        var updatedAt: String?
    ) : BaseBean

}