package com.skyd.imomoe.model.impls

import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.interfaces.IUtil
import com.skyd.imomoe.util.Util

class Util : IUtil {
    override fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        val const = DataSourceManager.getConst() ?: Const()
        return const.actionUrl.ANIME_DETAIL() + episodeUrl
            .replaceFirst(const.actionUrl.ANIME_PLAY(), "")
            .replaceFirst(Regex("-.*\\.html"), "") + Util.getWebsiteLinkSuffix()
    }
}
