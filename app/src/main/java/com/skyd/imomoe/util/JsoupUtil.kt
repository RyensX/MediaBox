package com.skyd.imomoe.util

import com.skyd.imomoe.config.Const
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

object JsoupUtil {
    fun getDocument(url: String): Document =
        Jsoup.connect(url)
            .userAgent(Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)])
            .get()
}