package com.skyd.imomoe.model.impls

import com.skyd.imomoe.model.interfaces.IUtil

class Util : IUtil {
    override fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        return ".html"
    }
}
