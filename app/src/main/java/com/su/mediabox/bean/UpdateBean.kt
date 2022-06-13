package com.su.mediabox.bean

import com.google.gson.annotations.SerializedName

class UpdateBean(
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

) {

    class AssetsBean(
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
    )

}