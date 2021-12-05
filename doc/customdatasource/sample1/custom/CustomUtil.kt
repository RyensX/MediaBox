package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.model.interfaces.IUtil
import com.skyd.imomoe.util.Util

class CustomUtil : IUtil {
    override fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        val const = CustomConst()
        return const.actionUrl.ANIME_DETAIL() + episodeUrl
            .replaceFirst(const.actionUrl.ANIME_PLAY(), "")
            .replaceFirst(Regex("-.*\\.html"), "") + Util.getWebsiteLinkSuffix()
    }
}
