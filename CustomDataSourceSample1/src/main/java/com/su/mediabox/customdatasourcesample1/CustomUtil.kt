package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.interfaces.IUtil

object CustomUtil : IUtil {
    override fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        val const = CustomConst
        return const.ANIME_DETAIL + episodeUrl
            .replaceFirst(const.ANIME_PLAY, "")
            .replaceFirst(Regex("-.*\\.html"), "") + ".html"
    }
}
