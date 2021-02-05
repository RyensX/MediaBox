package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDetailBean
import com.skyd.imomoe.bean.AnimeDetailDataBean
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.ParseHtmlUtil.parseBotit
import com.skyd.imomoe.util.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.ParseHtmlUtil.parseImg
import com.skyd.imomoe.util.ParseHtmlUtil.parseMovurls
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import kotlin.collections.ArrayList


class AnimeDetailViewModel : ViewModel() {
    private var requestTimes = 0
    var animeDetailBean: AnimeDetailBean? = null
    var animeDetailBeanDataList: MutableList<AnimeDetailDataBean> = ArrayList()
    var mldAnimeDetailData: MutableLiveData<AnimeDetailBean> = MutableLiveData()

    //www.yhdm.io
    fun getAnimeDetailData(partUrl: String) {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + partUrl).get()
                animeDetailBeanDataList.clear()
                //番剧头部信息
                val area: Elements = document.getElementsByClass("area")
                for (i in area.indices) {
                    val areaChildren = area[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "fire l" -> {
                                var cover = ""
                                var title = ""
                                var alias = ""
                                var info = ""
                                var year = ""
                                var index = ""
                                var animeArea = ""
                                val animeType: MutableList<String> = ArrayList()
                                val tag: MutableList<String> = ArrayList()

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
                                                animeType.add(typeElements[l].text())
                                            }
                                            val tagElements: Elements = span[4].select("a")
                                            for (l in tagElements.indices) {
                                                tag.add(tagElements[l].text())
                                            }
                                        }
                                        "tabs" -> {     //播放列表+header
                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "header1", "",
                                                    fireLChildren[k].select("[class=tabs]")
                                                        .select("[class=menu0]")
                                                        .select("li").text(),
                                                    "",
                                                    "",
                                                    null
                                                )
                                            )

                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "animeEpisodeFlowLayout1",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    parseMovurls(
                                                        fireLChildren[k]
                                                            .select("[class=main0]")
                                                            .select("[class=movurl]")[0],
                                                        null
                                                    )
                                                )
                                            )
                                        }
                                        "botit" -> {     //其它header
                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "header1", "",
                                                    parseBotit(fireLChildren[k]),
                                                    "",
                                                    "",
                                                    null
                                                )
                                            )
                                        }
                                        "dtit" -> {     //其它header
                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "header1", "",
                                                    parseDtit(fireLChildren[k]),
                                                    "",
                                                    "",
                                                    null
                                                )
                                            )
                                        }
                                        "info" -> {         //动漫介绍
                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "animeDescribe1", "",
                                                    "",
                                                    "",
                                                    fireLChildren[k]
                                                        .select("[class=info]").text(),
                                                    null
                                                )
                                            )
                                        }
                                        "img" -> {         //系列动漫推荐
                                            animeDetailBeanDataList.add(
                                                AnimeDetailDataBean(
                                                    "gridRecyclerView1", "",
                                                    "",
                                                    "",
                                                    "",
                                                    null,
                                                    parseImg(fireLChildren[k])
                                                )
                                            )
                                        }
                                    }
                                }
                                animeDetailBean = AnimeDetailBean(
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
                                    info,
                                    animeDetailBeanDataList
                                )
                            }
                        }
                    }
                }
                mldAnimeDetailData.postValue(animeDetailBean)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getAnimeDetailData(partUrl)
                    } else requestTimes = 0
                }
                Log.e(TAG, e.message ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    companion object {
        const val TAG = "AnimeDetailViewModel"
    }
}