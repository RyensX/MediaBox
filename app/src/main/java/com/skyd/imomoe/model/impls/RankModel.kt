package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.model.JsoupUtil
import com.skyd.imomoe.model.ParseHtmlUtil
import com.skyd.imomoe.model.interfaces.IRankModel
import com.skyd.imomoe.model.util.Pair
import org.jsoup.select.Elements
import java.util.*

class RankModel : IRankModel {
    private var bgTimes = 0
    var tabList: ArrayList<TabBean> = ArrayList()
    var rankList: ArrayList<List<AnimeCoverBean>> = ArrayList()

    override fun getRankData(): Pair<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>> {
        tabList.clear()
        rankList.clear()
        getWeekRankData()
        getAllRankData()
        return Pair(tabList, rankList)
    }

    private fun getAllRankData() {
        val document = JsoupUtil.getDocument(Api.MAIN_URL + Const.ActionUrl.ANIME_TOP)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "gohome" -> {
                    tabList.add(
                        tabList.size, TabBean(
                            "",
                            "",
                            "",
                            areaChildren[i].select("h1").select("a").text()
                        )
                    )
                }
                "topli" -> {
                    rankList.add(rankList.size, ParseHtmlUtil.parseTopli(areaChildren[i]))
                }
            }
        }
    }

    private fun getWeekRankData() {
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
                                        "dtit" -> {
                                            tabList.add(
                                                0,
                                                TabBean(
                                                    "",
                                                    "",
                                                    "",
                                                    ParseHtmlUtil.parseDtit(bgChildren[k])
                                                )
                                            )
                                        }
                                        "pics" -> {
                                            rankList.add(0,
                                                ParseHtmlUtil.parsePics(bgChildren[k], url)
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
