package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.bean.IAnimeShowBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.html.JsoupUtil
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseDnews
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseHeroWrap
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseImg
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseNextPages
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseTopli
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
    var animeShowList: MutableList<IAnimeShowBean> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Int> = MutableLiveData()   // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null
    var newPageIndex: Pair<Int, Int>? = null

    private var isRequesting = false

    //http://www.yhdm.io版本
    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                pageNumberBean = null
                val url = Api.MAIN_URL + partUrl
                val document = JsoupUtil.getDocument(url)
                if (isRefresh) animeShowList.clear()
                val positionStart = animeShowList.size
                //banner
                val foucsBgElements: Elements = document.getElementsByClass("foucs bg")
                for (i in foucsBgElements.indices) {
                    val foucsBgChildren: Elements = foucsBgElements[i].children()
                    for (j in foucsBgChildren.indices) {
                        when (foucsBgChildren[j].className()) {
                            "hero-wrap" -> {
                                animeShowList.add(
                                    AnimeShowBean(
                                        Const.ViewHolderTypeString.BANNER_1, "",
                                        "", "", "", null, "",
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
                                            Const.ViewHolderTypeString.HEADER_1,
                                            "",
                                            "",
                                            elements[j].select("h2").text(),
                                            "",
                                            null,
                                            ""
                                        )
                                    )
                                } else {        //有右侧“更多”
                                    animeShowList.add(
                                        AnimeShowBean(
                                            Const.ViewHolderTypeString.HEADER_1,
                                            a.attr("href"),
                                            Api.MAIN_URL + a.attr("href"),
                                            a.text(),
                                            elements[j].select("span").select("a").text(),
                                            null,
                                            ""
                                        )
                                    )
                                }
                            }
                            "img", "imgs" -> {
                                animeShowList.addAll(parseImg(elements[j], url))
                            }
                            "fire l" -> {       //右侧前半tab内容
                                val firsLChildren = elements[j].children()
                                for (k in firsLChildren.indices) {
                                    when (firsLChildren[k].className()) {
                                        "lpic" -> {
                                            animeShowList.addAll(parseLpic(firsLChildren[k], url))
                                        }
                                        "pages" -> {
                                            pageNumberBean = parseNextPages(firsLChildren[k])
                                        }
                                    }
                                }
                            }
                            "dnews" -> {       //右侧后半tab内容，cover4
                                animeShowList.addAll(parseDnews(elements[j], url))
                            }
                            "topli" -> {       //右侧后半tab内容，cover5
                                animeShowList.addAll(parseTopli(elements[j]))
                            }
                            "pages" -> {
                                pageNumberBean = parseNextPages(elements[j])
                            }
                        }
                    }
                }
                newPageIndex = Pair(positionStart, animeShowList.size - positionStart)
                mldGetAnimeShowList.postValue(if (isRefresh) 0 else 1)
                isRequesting = false
            } catch (e: Exception) {
                animeShowList.clear()
                mldGetAnimeShowList.postValue(-1)
                isRequesting = false
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "AnimeShowViewModel"
    }
}