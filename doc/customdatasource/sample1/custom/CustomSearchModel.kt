package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.util.Util
import org.jsoup.select.Elements

class CustomSearchModel : ISearchModel {
    override suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val const = CustomConst()
        var pageNumberBean: PageNumberBean? = null
        val searchResultList: ArrayList<AnimeCoverBean> = ArrayList()
        val url =
            "${Api.MAIN_URL}${const.actionUrl.ANIME_SEARCH()}${Util.getEncodedUrl(keyWord)}/$partUrl"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(ParseHtmlUtil.parseLpic(lpic[0], url))
        val pages = lpic[0].select("[class=pages]")
        if (pages.size > 0) pageNumberBean = ParseHtmlUtil.parseNextPages(pages[0])
        return Pair(searchResultList, pageNumberBean)
    }
}