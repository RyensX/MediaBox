package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_TOP
import com.skyd.imomoe.util.JsoupUtil
import com.skyd.imomoe.util.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.ParseHtmlUtil.parsePics
import com.skyd.imomoe.util.ParseHtmlUtil.parseTopli
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class RankViewModel : ViewModel() {
    private var bgTimes = 0
    var tabList: MutableList<TabBean> = Collections.synchronizedList(ArrayList())
    var rankList: MutableList<List<AnimeCoverBean>> = Collections.synchronizedList(ArrayList())
    var mldRankData: MutableLiveData<Boolean> = MutableLiveData()
    private var getDataTimes = 0
        set(value) {
            if (value == 2) {
                field = 0
                mldRankData.postValue(true)
            } else {
                field = value
            }
        }

    fun getRankData() {
        GlobalScope.launch(Dispatchers.IO) {
            tabList.clear()
            rankList.clear()

            //以下两个方法的list的add时必须加锁才能保证tab和rankData顺序相对应
            //如：一周动漫排行榜都是第0个，动漫排行榜都是第0个
            getWeekRankData()
            getAllRankData()
        }
    }

    private fun getAllRankData() {
        try {
            val document = JsoupUtil.getDocument(Api.MAIN_URL + ANIME_TOP)
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
                        rankList.add(rankList.size, parseTopli(areaChildren[i]))
                    }
                }
            }
            getDataTimes++
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.showToastOnThread(Toast.LENGTH_LONG)
        }
    }

    private fun getWeekRankData() {
        bgTimes = 0
        try {
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
                                                        parseDtit(bgChildren[k])
                                                    )
                                                )
                                            }
                                            "pics" -> {
                                                rankList.add(0, parsePics(bgChildren[k], url))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            getDataTimes++
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.showToastOnThread(Toast.LENGTH_LONG)
        }
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}