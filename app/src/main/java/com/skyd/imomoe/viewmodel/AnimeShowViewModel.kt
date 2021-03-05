package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.JsoupUtil
import com.skyd.imomoe.util.ParseHtmlUtil.parseDnews
import com.skyd.imomoe.util.ParseHtmlUtil.parseHeroWrap
import com.skyd.imomoe.util.ParseHtmlUtil.parseImg
import com.skyd.imomoe.util.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.ParseHtmlUtil.parseTopli
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class AnimeShowViewModel : ViewModel() {
    var childViewPool: SerializableRecycledViewPool? = null
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<AnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Boolean> = MutableLiveData()

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                val url = Api.MAIN_URL + partUrl
                val document = JsoupUtil.getDocument(url)
                animeShowList.clear()
                //banner
                val foucsBgElements: Elements = document.getElementsByClass("foucs bg")
                for (i in foucsBgElements.indices) {
                    val foucsBgChildren: Elements = foucsBgElements[i].children()
                    for (j in foucsBgChildren.indices) {
                        when (foucsBgChildren[j].className()) {
                            "hero-wrap" -> {
                                animeShowList.add(
                                    AnimeShowBean(
                                        "banner1", "",
                                        "", "", "", "", "",
                                        parseHeroWrap(foucsBgChildren[j], url)
                                    )
                                )
                            }
                        }
                    }
                }
                //area
                var area: Elements = document.getElementsByClass("area")
                if (partUrl == "/") //首页，有右边栏
                    area = document.getElementsByClass("area").select("[class=firs l]")
                for (i in area.indices) {
                    val elements: Elements = area[i].children()
                    for (j in elements.indices) {
                        when (elements[j].className()) {
                            "dtit" -> {
                                val a = elements[j].select("h2").select("a")
                                if (a.size == 0) {      //只有一个标题
                                    animeShowList.add(
                                        AnimeShowBean(
                                            "header1",
                                            "",
                                            "",
                                            elements[j].select("h2").text(),
                                            "",
                                            "",
                                            ""
                                        )
                                    )
                                } else {        //有右侧“更多”
                                    animeShowList.add(
                                        AnimeShowBean(
                                            "header1",
                                            a.attr("href"),
                                            Api.MAIN_URL + a.attr("href"),
                                            a.text(),
                                            elements[j].select("span").select("a").text(),
                                            "",
                                            ""
                                        )
                                    )
                                }
                            }
                            "img", "imgs" -> {
                                animeShowList.add(
                                    AnimeShowBean(
                                        "gridRecyclerView1",
                                        "", "", "", "", "", "",
                                        parseImg(elements[j], url)
                                    )
                                )
                            }
                            "fire l" -> {       //右侧前半tab内容
                                val firsLChildren = elements[j].children()
                                for (k in firsLChildren.indices) {
                                    when (firsLChildren[k].className()) {
                                        "lpic" -> {
                                            animeShowList.add(
                                                AnimeShowBean(
                                                    "gridRecyclerView1",
                                                    "", "", "", "", "", "",
                                                    parseLpic(firsLChildren[k], url)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            "dnews" -> {       //右侧后半tab内容，cover4
                                animeShowList.add(
                                    AnimeShowBean(
                                        "gridRecyclerView1",
                                        "", "", "", "", "", "",
                                        parseDnews(elements[j], url)
                                    )
                                )
                            }
                            "topli" -> {       //右侧后半tab内容，cover5
                                animeShowList.add(
                                    AnimeShowBean(
                                        "gridRecyclerView1",
                                        "", "", "", "", "", "",
                                        parseTopli(elements[j])
                                    )
                                )
                            }
                        }
                    }
                }
                mldGetAnimeShowList.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            } finally {
                isRequesting = false
            }
        }
    }

    companion object {
        const val TAG = "AnimeShowViewModel"
    }
}