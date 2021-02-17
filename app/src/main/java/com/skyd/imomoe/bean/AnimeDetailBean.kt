package com.skyd.imomoe.bean

class AnimeInfoBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var cover: String,
    var alias: String,
    var area: String,
    var year: String,
    var index: String,
    var animeType: List<AnimeTypeBean>,
    var tag: List<AnimeTypeBean>,
    var info: String,
) : BaseBean

//番剧详情下方信息rv数据，播放页面下方rv数据
class AnimeDetailBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var describe: String,
    var episodeList: List<AnimeEpisodeDataBean>? = null,
    var animeCoverList: List<AnimeCoverBean>? = null,
    var headerInfo: AnimeInfoBean? = null
) : BaseBean

//每一集
class AnimeEpisodeDataBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var videoUrl: String = ""
) : BaseBean