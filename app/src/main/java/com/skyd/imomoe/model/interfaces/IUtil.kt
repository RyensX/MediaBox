package com.skyd.imomoe.model.interfaces

/**
 * 工具类
 */
interface IUtil : IBase {
    /**
     * 通过播放页面的网址获取详情页面的网址
     *
     * @param episodeUrl 播放页面的网址，不为null
     * @return 详情页面的网址，不可为null
     */
    fun getDetailLinkByEpisodeLink(episodeUrl: String): String

    companion object {
        const val implName = "Util"
    }
}