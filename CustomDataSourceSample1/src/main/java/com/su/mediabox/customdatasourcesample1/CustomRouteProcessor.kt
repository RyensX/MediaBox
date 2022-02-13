package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.customdatasourcesample1.Text.isYearMonth
import com.su.mediabox.plugin.AppUtil
import com.su.mediabox.plugin.Constant
import com.su.mediabox.plugin.Text.buildRouteActionUrl
import com.su.mediabox.plugin.Text.getSubString
import com.su.mediabox.plugin.UI.toast
import com.su.mediabox.plugin.interfaces.IRouteProcessor
import java.net.URLDecoder

class CustomRouteProcessor : IRouteProcessor {

    override fun process(actionUrl: String): Boolean {
        val processor = AppUtil.appProcessor ?: return false
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = CustomConst
        var solved = true

        //映射网站地址到本地action
        when {
            //分类
            decodeUrl.startsWith(Constant.ActionUrl.ANIME_CLASSIFY) -> {
                val paramList = actionUrl.replace(Constant.ActionUrl.ANIME_CLASSIFY, "").split("/")
                if (paramList.size == 3) {      //例如  /japan/日本  分割后是3个参数：""，japan，日本
                    processor.process(
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_CLASSIFY,
                            paramList[1], paramList[2]
                        )
                    )
                } else "跳转协议格式错误".toast()
            }
            decodeUrl.replace("/", "").isYearMonth() -> {
                processor.process(
                    buildRouteActionUrl(
                        Constant.ActionUrl.ANIME_MONTH_NEW_ANIME,
                        actionUrl
                    )
                )//如201907月新番列表
            }
            //排行榜
            decodeUrl.startsWith(const.ANIME_RANK) -> {
                processor.process(buildRouteActionUrl(Constant.ActionUrl.ANIME_RANK))
            }
            //进入搜索页面
            decodeUrl.startsWith(const.ANIME_SEARCH) -> {
                decodeUrl.replace(const.ANIME_SEARCH, "").let {
                    val keyWord = it.replaceFirst(Regex("/.*"), "")
                    val pageNumber = it.replaceFirst(Regex("($keyWord/)|($keyWord)"), "")
                    processor.process(
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_SEARCH,
                            keyWord, pageNumber
                        )
                    )
                }
            }
            //番剧封面点击进入
            decodeUrl.startsWith(const.ANIME_DETAIL) -> {
                processor.process(buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, actionUrl))
            }
            //番剧每一集点击进入
            decodeUrl.startsWith(const.ANIME_PLAY) -> {
                val playCode = actionUrl.getSubString("\\/v\\/", "\\.")[0].split("-")
                if (playCode.size >= 2) {
                    var detailPartUrl =
                        actionUrl.substringAfter(const.ANIME_DETAIL, "")
                    detailPartUrl = const.ANIME_DETAIL + detailPartUrl
                    processor.process(
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_PLAY,
                            actionUrl.substringBefore(const.ANIME_DETAIL),
                            detailPartUrl
                        )
                    )
                } else {
                    "播放集数解析错误！".toast()
                }
            }
            else -> solved = false
        }
        return solved
    }
}
