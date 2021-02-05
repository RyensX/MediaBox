package com.skyd.imomoe.bean

class AnimeDetailBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var cover: String,
    var alias: String,
    var area: String,
    var year: String,
    var index: String,
    var animeType: List<String>,
    var tag: List<String>,
    var info: String,
    var data: List<AnimeDetailDataBean>
) : BaseBean

//番剧详情下方信息rv数据，播放页面下方rv数据
class AnimeDetailDataBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var cover: String,
    var describe: String,
    var episodeList: List<AnimeEpisodeDataBean>?,
    var animeCoverList: List<AnimeCoverBean>? = null
) : BaseBean

//每一集
class AnimeEpisodeDataBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var videoUrl: String = ""
) : BaseBean