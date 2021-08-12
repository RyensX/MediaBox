package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel
import org.jsoup.select.Elements

class CustomMonthAnimeModel : IMonthAnimeModel {
    override suspend fun getMonthAnimeData(
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val monthAnimeList: ArrayList<AnimeCoverBean> = ArrayList()
        val url = Api.MAIN_URL + partUrl
        var pageNumberBean: PageNumberBean? = null
        val document = JsoupUtil.getDocument(url)
        val area: Elements = document.getElementsByClass("area")
        for (i in area.indices) {
            val elements: Elements = area[i].children()
            for (j in elements.indices) {
                when (elements[j].className()) {
                    "img", "imgs" -> {
                        monthAnimeList.addAll(
                            CustomParseHtmlUtil.parseImg(
                                elements[j],
                                url
                            )
                        )
                    }
                    "fire l" -> {       //右侧前半tab内容
                        val firsLChildren = elements[j].children()
                        for (k in firsLChildren.indices) {
                            when (firsLChildren[k].className()) {
                                "lpic" -> {
                                    monthAnimeList.addAll(
                                        CustomParseHtmlUtil.parseLpic(
                                            firsLChildren[k],
                                            url
                                        )
                                    )
                                }
                                "pages" -> {
                                    pageNumberBean =
                                        CustomParseHtmlUtil.parseNextPages(
                                            firsLChildren[k]
                                        )
                                }
                            }
                        }
                    }
                    "dnews" -> {       //右侧后半tab内容，cover4
                        monthAnimeList.addAll(
                            CustomParseHtmlUtil.parseDnews(
                                elements[j],
                                url
                            )
                        )
                    }
                    "topli" -> {       //右侧后半tab内容，cover5
                        monthAnimeList.addAll(
                            CustomParseHtmlUtil.parseTopli(
                                elements[j]
                            )
                        )
                    }
                    "pages" -> {
                        pageNumberBean =
                            CustomParseHtmlUtil.parseNextPages(
                                elements[j]
                            )
                    }
                }
            }
        }
        return Pair(monthAnimeList, pageNumberBean)
    }
}