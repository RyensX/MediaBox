package com.su.mediabox.plugin.standard.been

class PlayBean(
    override var type: String,
    override var actionUrl: String,
    var title: AnimeTitleBean,
    var episode: AnimeEpisodeDataBean,
    var data: List<IAnimeDetailBean>
) : BaseBean

//番剧详情下方信息rv数据
class AnimeTitleBean(
    override var type: String,
    override var actionUrl: String,
    var title: String
) : BaseBean