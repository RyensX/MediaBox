package com.skyd.imomoe.model

import com.skyd.imomoe.config.Const
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

object JsoupUtil {
    /**
     * 获取没有运行js的html
     */
    fun getDocument(url: String): Document =
        Jsoup.connect(url)
            .userAgent(Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)])
            .get()
}