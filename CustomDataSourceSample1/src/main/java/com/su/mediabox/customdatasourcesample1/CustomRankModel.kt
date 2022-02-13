package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.interfaces.IRankModel
import com.su.mediabox.plugin.standard.been.TabBean
import org.jsoup.select.Elements

class CustomRankModel : IRankModel {
    private var bgTimes = 0
    private var tabList: ArrayList<TabBean> = ArrayList()

    override suspend fun getRankTabData(): ArrayList<TabBean> {
        tabList.clear()
        getWeekRankData()
        getAllRankData()
        return tabList
    }

    private suspend fun getAllRankData() {
        val const = CustomConst
        val document = JsoupUtil.getDocument(const.MAIN_URL() + const.ANIME_RANK)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "gohome" -> {
                    tabList.add(
                        tabList.size, TabBean(
                            "",
                            const.ANIME_RANK,
                            "",
                            areaChildren[i].select("h1").select("a").text()
                        )
                    )
                }
            }
        }
    }

    private suspend fun getWeekRankData() {
        bgTimes = 0
        val url = CustomConst.MAIN_URL()
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
                                        "dtit" -> {
                                            tabList.add(
                                                0,
                                                TabBean(
                                                    "",
                                                    "/",
                                                    "",
                                                    ParseHtmlUtil.parseDtit(bgChildren[k])
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
