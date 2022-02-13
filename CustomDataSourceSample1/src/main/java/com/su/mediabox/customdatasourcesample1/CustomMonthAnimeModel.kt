package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.interfaces.IMonthAnimeModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean
import org.jsoup.select.Elements

class CustomMonthAnimeModel : IMonthAnimeModel {
    override suspend fun getMonthAnimeData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val monthAnimeList: ArrayList<AnimeCoverBean> = ArrayList()
        val url = CustomConst.MAIN_URL() + partUrl
        val document = JsoupUtil.getDocument(url)
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "lpic" -> {
                        monthAnimeList.addAll(ParseHtmlUtil.parseLpic(areaChildren[j], url))
                    }
                }
            }
        }
        return Pair(monthAnimeList, null)
    }
}