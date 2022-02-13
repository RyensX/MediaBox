package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.Constant
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object JsoupUtil {
    /**
     * 获取没有运行js的html
     */
    suspend fun getDocument(url: String): Document =
        runCatching { getDocumentSynchronously(url) }.getOrThrow()

    fun getDocumentSynchronously(url: String): Document = jsoupRandomHeaderPase(url)

    fun jsoupRandomHeaderPase(url: String) = Jsoup.connect(url)
        .data("User-Agent", Constant.Request.getRandomUserAgent())
        .get()

}