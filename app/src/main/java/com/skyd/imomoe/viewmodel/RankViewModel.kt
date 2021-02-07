package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_TOP
import com.skyd.imomoe.util.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.ParseHtmlUtil.parsePics
import com.skyd.imomoe.util.ParseHtmlUtil.parseTlist
import com.skyd.imomoe.util.ParseHtmlUtil.parseTopli
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class RankViewModel : ViewModel() {
    private var requestTimes = 0
    private var bgTimes = 0
    var tabList: MutableList<TabBean> = ArrayList()
    var rankList: MutableList<List<AnimeCoverBean>> = ArrayList()
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
        try {
            tabList.clear()
            rankList.clear()

            //以下两个方法的list的add时必须加锁才能保证tab和rankData顺序相对应
            //如：一周动漫排行榜都是第0个，动漫排行榜都是第0个
            getWeekRankData()
            getAllRankData()
        } catch (e: HttpStatusException) {
            e.printStackTrace()
            if (e.statusCode == 502) {
                if (requestTimes <= 2) {
                    requestTimes++
                    getRankData()
                } else requestTimes = 0
            }
            Log.e(TAG, e.message ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
        }
    }

    private fun getAllRankData() {
        Thread {
            val document = Jsoup.connect(Api.MAIN_URL + ANIME_TOP).get()
            val areaChildren: Elements = document.select("[class=area]")[0].children()
            for (i in areaChildren.indices) {
                when (areaChildren[i].className()) {
                    "gohome" -> {
                        synchronized(this) {
                            tabList.add(
                                tabList.size, TabBean(
                                    "",
                                    "",
                                    "",
                                    areaChildren[i].select("h1").select("a").text()
                                )
                            )
                        }
                    }
                    "topli" -> {
                        synchronized(this) {
                            rankList.add(rankList.size, parseTopli(areaChildren[i]))
                        }
                    }
                }
            }
            getDataTimes++
        }.start()
    }

    private fun getWeekRankData() {
        bgTimes = 0
        Thread {
            val document = Jsoup.connect(Api.MAIN_URL).get()
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
                                                synchronized(this) {
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
                                            }
                                            "pics" -> {
                                                synchronized(this) {
                                                    rankList.add(0, parsePics(bgChildren[k]))
                                                }
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
        }.start()
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}