package com.skyd.imomoe.util

import com.skyd.imomoe.config.Const
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object JsoupUtil {
    fun getDocument(url: String): Document =
        Jsoup.connect(url).userAgent(Const.Request.USER_AGENT).get()
}