package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDetailBean
import com.skyd.imomoe.bean.AnimeInfoBean
import com.skyd.imomoe.bean.AnimeTypeBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.ParseHtmlUtil.parseBotit
import com.skyd.imomoe.util.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.ParseHtmlUtil.parseImg
import com.skyd.imomoe.util.ParseHtmlUtil.parseMovurls
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import kotlin.collections.ArrayList


class AnimeDetailViewModel : ViewModel() {
    var cover: String = ""
    var title: String = ""
    var animeDetailList: MutableList<AnimeDetailBean> = ArrayList()
    var mldAnimeDetailList: MutableLiveData<Boolean> = MutableLiveData()

    //www.yhdm.io
    fun getAnimeDetailData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + partUrl).get()
                animeDetailList.clear()
                //番剧头部信息
                val area: Elements = document.getElementsByClass("area")
                for (i in area.indices) {
                    val areaChildren = area[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "fire l" -> {
                                var alias = ""
                                var info = ""
                                var year = ""
                                var index = ""
                                var animeArea = ""
                                val animeType: MutableList<AnimeTypeBean> = ArrayList()
                                val tag: MutableList<AnimeTypeBean> = ArrayList()

                                val fireLChildren =
                                    areaChildren[j].select("[class=fire l]")[0].children()
                                for (k in fireLChildren.indices) {
                                    when (fireLChildren[k].className()) {
                                        "thumb l" -> {
                                            cover = fireLChildren[k]
                                                .select("img").attr("src")
                                        }
                                        "rate r" -> {
                                            val rateR = fireLChildren[k]
                                            title = rateR.select("h1").text()
                                            val sinfo: Elements = rateR.select("[class=sinfo]")
                                            val span: Elements = sinfo.select("span")
                                            val p: Elements = sinfo.select("p")
                                            if (p.size == 1) {
                                                alias = p[0].text()
                                            } else if (p.size == 2) {
                                                alias = p[0].text()
                                                info = p[1].text()
                                            }
                                            year = span[0].text()
                                            animeArea = span[1].select("a").text()
                                            index = span[3].select("a").text()
                                            val typeElements: Elements = span[2].select("a")
                                            for (l in typeElements.indices) {
                                                animeType.add(
                                                    AnimeTypeBean(
                                                        "",
                                                        typeElements[l].attr("href"),
                                                        Api.MAIN_URL + typeElements[l].attr("href"),
                                                        typeElements[l].text()
                                                    )
                                                )
                                            }
                                            val tagElements: Elements = span[4].select("a")
                                            for (l in tagElements.indices) {
                                                tag.add(
                                                    AnimeTypeBean(
                                                        "",
                                                        tagElements[l].attr("href"),
                                                        Api.MAIN_URL + tagElements[l].attr("href"),
                                                        tagElements[l].text()
                                                    )
                                                )
                                            }
                                        }
                                        "tabs" -> {     //播放列表+header
                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "header1", "",
                                                    fireLChildren[k].select("[class=tabs]")
                                                        .select("[class=menu0]")
                                                        .select("li").text(),
                                                    "",
                                                    null
                                                )
                                            )

                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "horizontalRecyclerView1",
                                                    "",
                                                    "",
                                                    "",
                                                    parseMovurls(
                                                        fireLChildren[k]
                                                            .select("[class=main0]")
                                                            .select("[class=movurl]")[0],
                                                        null,
                                                        type = "animeEpisode2"
                                                    )
                                                )
                                            )
                                        }
                                        "botit" -> {     //其它header
                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "header1", "",
                                                    parseBotit(fireLChildren[k]),
                                                    "",
                                                    null
                                                )
                                            )
                                        }
                                        "dtit" -> {     //其它header
                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "header1", "",
                                                    parseDtit(fireLChildren[k]),
                                                    "",
                                                    null
                                                )
                                            )
                                        }
                                        "info" -> {         //动漫介绍
                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "animeDescribe1", "",
                                                    "",
                                                    fireLChildren[k]
                                                        .select("[class=info]").text(),
                                                    null
                                                )
                                            )
                                        }
                                        "img" -> {         //系列动漫推荐
                                            animeDetailList.add(
                                                AnimeDetailBean(
                                                    "gridRecyclerView1", "",
                                                    "",
                                                    "",
                                                    null,
                                                    parseImg(fireLChildren[k])
                                                )
                                            )
                                        }
                                    }
                                }
                                val animeInfoBean = AnimeInfoBean(
                                    "",
                                    "",
                                    title,
                                    cover,
                                    alias,
                                    animeArea,
                                    year,
                                    index,
                                    animeType,
                                    tag,
                                    info
                                )
                                animeDetailList.add(
                                    0,
                                    AnimeDetailBean(
                                        "animeInfo1", "",
                                        "",
                                        "",
                                        headerInfo = animeInfoBean
                                    )
                                )
                            }
                        }
                    }
                }
                mldAnimeDetailList.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "AnimeDetailViewModel"
    }
}