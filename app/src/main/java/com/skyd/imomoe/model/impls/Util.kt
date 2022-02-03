package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IUtil

class Util : IUtil {
    override fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        return ".html"
    }
}
