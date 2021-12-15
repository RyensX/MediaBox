package com.skyd.imomoe.model.util

import com.skyd.imomoe.config.Const
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.HtmlService
import com.skyd.imomoe.util.string
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

object JsoupUtil {
    /**
     * 获取没有运行js的html
     */
    suspend fun getDocument(url: String): Document {
        return Jsoup.parse(
            RetrofitManager.instance.create(HtmlService::class.java).getHtml(
                url,
                Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
            ).byteStream().string()
        )
    }

    fun getDocumentSynchronously(url: String): Document {
        return Jsoup.parse(
            RetrofitManager.instance.create(HtmlService::class.java).getHtmlSynchronously(
                url,
                Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
            ).execute().body()?.byteStream()?.string() ?: ""
        )
    }
}