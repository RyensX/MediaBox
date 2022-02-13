package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.interfaces.IEverydayAnimeModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.AnimeShowBean
import com.su.mediabox.plugin.standard.been.TabBean
import org.jsoup.select.Elements

class CustomEverydayAnimeModel : IEverydayAnimeModel {
    override suspend fun getEverydayAnimeData(): Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, AnimeShowBean> {
        val tabList = ArrayList<TabBean>()
        val header = AnimeShowBean(
            "", "", "", "",
            "", null, "", null
        )
        val everydayAnimeList: ArrayList<List<AnimeCoverBean>> = ArrayList()
        val document = JsoupUtil.getDocument(CustomConst.MAIN_URL())
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "side r" -> {
                    val sideRChildren = areaChildren[i].children()
                    out@ for (j in sideRChildren.indices) {
                        when (sideRChildren[j].className()) {
                            "bg" -> {
                                val bgChildren = sideRChildren[j].children()
                                for (k in bgChildren.indices) {
                                    when (bgChildren[k].className()) {
                                        "dtit" -> {
                                            header.title = ParseHtmlUtil.parseDtit(bgChildren[k])
                                        }
                                        "tag" -> {
                                            val tagChildren = bgChildren[k].children()
                                            for (l in tagChildren.indices) {
                                                tabList.add(
                                                    TabBean(
                                                        "",
                                                        "",
                                                        "",
                                                        tagChildren[l].text()
                                                    )
                                                )
                                            }
                                        }
                                        "tlist" -> {
                                            everydayAnimeList.addAll(
                                                ParseHtmlUtil.parseTlist(
                                                    bgChildren[k]
                                                )
                                            )
                                        }
                                    }
                                }
                                break@out
                            }
                        }
                    }
                }
            }
        }
        return Triple(tabList, everydayAnimeList, header)
    }
}