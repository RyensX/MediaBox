package com.su.mediabox.plugin.standard.been

import com.google.gson.annotations.SerializedName

class AnimeShowBean(
    override var type: String,
    override var actionUrl: String,
    override var url: String,
    override var title: String,
    override var rTitle: String,      //右侧更多等...
    override var cover: ImageBean?,
    override var episode: String,
    override var animeCoverList: List<AnimeCoverBean>? = null
) : BaseBean, IAnimeShowBean

interface IAnimeShowBean : BaseBean {
    var url: String
    var title: String
    var rTitle: String      //右侧更多等...
    var cover: ImageBean?
    var episode: String
    var animeCoverList: List<AnimeCoverBean>?
}

class AnimeCoverBean(
    override var type: String,
    override var actionUrl: String,
    override var url: String,
    override var title: String,
    override var cover: ImageBean?,
    override var episode: String,
    var animeType: List<AnimeTypeBean>? = null,
    override var describe: String? = null,
    var episodeClickable: AnimeEpisodeDataBean? = null,
    var area: AnimeAreaBean? = null,
    var date: String? = null,
    var size: String? = null,           //视频大小，如300M
    var episodeCount: String? = null,    //集数
    // 0：/storage/emulated/0/Android/data/packname/files
    // 1：/storage/emulated/0/
    var path: Int = 0,
    override var rTitle: String = "",
    override var animeCoverList: List<AnimeCoverBean>? = null,
    override var episodeList: List<AnimeEpisodeDataBean>? = null,
    override var headerInfo: AnimeInfoBean? = null
) : BaseBean, IAnimeShowBean, IAnimeDetailBean

class AnimeTypeBean(       //番剧类型：包括类型名和链接
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean

class AnimeAreaBean(       //番剧地区：包括地区名和链接
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean

class ImageBean(       //图片bean，带有referer信息
    @SerializedName("type")
    override var type: String,

    @SerializedName("actionUrl")
    override var actionUrl: String,

    @SerializedName("url")
    var url: String,

    @SerializedName("referer")
    var referer: String
) : BaseBean