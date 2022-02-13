package com.su.mediabox.customdatasourcesample1

import android.net.Uri
import com.su.mediabox.plugin.interfaces.ISearchModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean
import org.jsoup.select.Elements

class CustomSearchModel : ISearchModel {
    override suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val const = CustomConst
        var pageNumberBean: PageNumberBean? = null
        val searchResultList: ArrayList<AnimeCoverBean> = ArrayList()
        val url =
            "${const.MAIN_URL()}${const.ANIME_SEARCH}${Uri.encode(keyWord, ":/-![].,%?&=")}/$partUrl"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(ParseHtmlUtil.parseLpic(lpic[0], url))
        val pages = lpic[0].select("[class=pages]")
        if (pages.size > 0) pageNumberBean = ParseHtmlUtil.parseNextPages(pages[0])
        return Pair(searchResultList, pageNumberBean)
    }
}