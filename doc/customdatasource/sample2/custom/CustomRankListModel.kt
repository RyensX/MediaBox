package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.interfaces.IRankListModel
import org.jsoup.select.Elements

class CustomRankListModel : IRankListModel {
    private var bgTimes = 0
    var rankList: MutableList<AnimeCoverBean> = ArrayList()
    var pageNumberBean: PageNumberBean? = null

    override suspend fun getRankListData(
        partUrl: String
    ): Pair<MutableList<AnimeCoverBean>, PageNumberBean?> {
        rankList.clear()
        if (partUrl == "/" || partUrl == "") getWeekRankData()
        else getAllRankData(partUrl)
        return Pair(rankList, pageNumberBean)
    }

    private suspend fun getAllRankData(partUrl: String) {
        val const = CustomConst()
        val document = JsoupUtil.getDocument(Api.MAIN_URL + partUrl)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "fire l" -> {       //右侧前半tab内容
                    val firsLChildren = areaChildren[i].children()
                    for (k in firsLChildren.indices) {
                        when (firsLChildren[k].className()) {
                            "lpic" -> {
                                rankList.addAll(
                                    CustomParseHtmlUtil.parseRankListLpic(
                                        firsLChildren[k],
                                        Api.MAIN_URL + const.actionUrl.ANIME_RANK()
                                    )
                                )
                            }
                            "pages" -> {
                                pageNumberBean =
                                    CustomParseHtmlUtil.parseNextPages(firsLChildren[k])
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getWeekRankData() {
        bgTimes = 0
        val url = Api.MAIN_URL
        val document = JsoupUtil.getDocument(url)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "side r" -> {
                    val sideRChildren = areaChildren[i].children()
                    for (j in sideRChildren.indices) {
                        when (sideRChildren[j].className()) {
                            "bg" -> {
                                if (bgTimes++ == 0) continue
                                val bgChildren = sideRChildren[j].children()
                                for (k in bgChildren.indices) {
                                    when (bgChildren[k].className()) {
                                        "pics" -> {
                                            rankList.addAll(
                                                CustomParseHtmlUtil.parsePics(
                                                    bgChildren[k],
                                                    url
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
