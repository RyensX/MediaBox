package com.skyd.imomoe.bean

class AnimeShowBean(
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String,
    var rTitle: String,      //右侧更多等...
    var cover: String,
    var episode: String,
    var animeCoverList: List<AnimeCoverBean>? = null
) : BaseBean

class AnimeCoverBean(       //番剧卡片
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var title: String,
    var cover: ImageBean?,
    var episode: String,
    var animeType: List<AnimeTypeBean>? = null,
    var describe: String? = null,
    var episodeClickable: AnimeEpisodeDataBean? = null,
    var area: AnimeAreaBean? = null,
    var date: String? = null,
    var size: String? = null,           //视频大小，如300M
    var episodeCount: String? = null    //集数
) : BaseBean

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
    override var type: String,
    override var actionUrl: String,
    var url: String,
    var referer: String
) : BaseBean