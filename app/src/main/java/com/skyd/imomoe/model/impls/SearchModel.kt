package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.util.ParseHtmlUtil
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.model.util.Pair
import org.jsoup.select.Elements
import java.net.URLEncoder
import java.util.ArrayList

class SearchModel : ISearchModel {
    override fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val const = DataSourceManager.getConst() ?: Const()
        var pageNumberBean: PageNumberBean? = null
        val searchResultList: ArrayList<AnimeCoverBean> = ArrayList()
        val url = Api.MAIN_URL + const.actionUrl.ANIME_SEARCH() + URLEncoder.encode(
            keyWord,
            "utf-8"
        ) + "/" + partUrl
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(ParseHtmlUtil.parseLpic(lpic[0], url))
        val pages = lpic[0].select("[class=pages]")
        if (pages.size > 0) pageNumberBean = ParseHtmlUtil.parseNextPages(pages[0])
        return Pair(searchResultList, pageNumberBean)
    }
}