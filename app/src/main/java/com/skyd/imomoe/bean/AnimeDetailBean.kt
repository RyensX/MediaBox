package com.skyd.imomoe.bean

class AnimeInfoBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var cover: ImageBean,
    var alias: String,
    var area: String,
    var year: String,
    var index: String,
    var animeType: List<AnimeTypeBean>,
    var tag: List<AnimeTypeBean>,
    var info: String
) : BaseBean

//番剧详情下方信息rv数据，播放页面下方rv数据
class AnimeDetailBean(
    override var type: String,
    override var actionUrl: String,
    override var title: String,
    override var describe: String?,
    override var episodeList: List<AnimeEpisodeDataBean>? = null,
    override var animeCoverList: List<AnimeCoverBean>? = null,
    override var headerInfo: AnimeInfoBean? = null
) : IAnimeDetailBean

interface IAnimeDetailBean : BaseBean {
    var title: String
    var describe: String?
    var episodeList: List<AnimeEpisodeDataBean>?
    var animeCoverList: List<AnimeCoverBean>?
    var headerInfo: AnimeInfoBean?
}

//每一集
class AnimeEpisodeDataBean(
    override var type: String,
    override var actionUrl: String,
    var title: String,
    var videoUrl: String = ""
) : BaseBean