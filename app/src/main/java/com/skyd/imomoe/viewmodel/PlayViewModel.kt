package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.ParseHtmlUtil
import com.skyd.imomoe.util.ParseHtmlUtil.parseBotit
import com.skyd.imomoe.util.ParseHtmlUtil.parseMovurls
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class PlayViewModel : ViewModel() {
    private var requestTimes = 0
    var playBean: PlayBean? = null
    var mldPlayBean: MutableLiveData<PlayBean> = MutableLiveData()
    var playBeanDataList: MutableList<AnimeDetailDataBean> = ArrayList()
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    val animeEpisodeDataBean = AnimeEpisodeDataBean("animeEpisode1", "", "")
    val mldAnimeEpisodeDataRefreshed: MutableLiveData<Boolean> = MutableLiveData()

    fun refreshAnimeEpisodeData(partUrl: String, title: String = "") {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + partUrl).get()
                val children: Elements = document.select("body")[0].children()
                for (i in children.indices) {
                    when (children[i].className()) {
                        "play" -> {
                            animeEpisodeDataBean.actionUrl = partUrl
                            animeEpisodeDataBean.title = title
                            animeEpisodeDataBean.videoUrl = children[i].select("[class=area]")
                                .select("[class=bofang]").select("div").attr("data-vid")
                                .replace("\$mp4", "")
                            break
                        }
                    }
                }
                mldAnimeEpisodeDataRefreshed.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                animeEpisodeDataBean.actionUrl = "animeEpisode1"
                animeEpisodeDataBean.title = ""
                animeEpisodeDataBean.videoUrl = ""
                mldAnimeEpisodeDataRefreshed.postValue(true)
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    fun getPlayData(partUrl: String) {
        Thread {
            try {
                val title = AnimeTitleBean("", "", "")
                val episode =
                    AnimeEpisodeDataBean(
                        "", "",
                        ""
                    )
                val document = Jsoup.connect(Api.MAIN_URL + partUrl).get()
                val children: Elements = document.allElements
                playBeanDataList.clear()
                episodesList.clear()
                for (i in children.indices) {
                    when (children[i].className()) {
                        "play" -> {
                            animeEpisodeDataBean.videoUrl = children[i].select("[class=area]")
                                .select("[class=bofang]").select("div").attr("data-vid")
                                .replace("\$mp4", "")
                        }
                        "area" -> {
                            val areaChildren = children[i].children()
                            for (j in areaChildren.indices) {
                                when (areaChildren[j].className()) {
                                    "gohome l" -> {        //标题
                                        title.title = areaChildren[j].select("h1")
                                            .select("a").text()
                                        title.actionUrl = areaChildren[j].select("h1")
                                            .select("a").attr("href")
                                        episode.title = areaChildren[j].select("h1")
                                            .select("span").text().replace("：", "")
                                    }
                                    "botit" -> {
                                        playBeanDataList.add(
                                            AnimeDetailDataBean(
                                                "header1",
                                                "",
                                                parseBotit(areaChildren[j]),
                                                "",
                                                "",
                                                null
                                            )
                                        )
                                    }
                                    "movurls" -> {      //集数列表
                                        playBeanDataList.add(
                                            AnimeDetailDataBean(
                                                "animeEpisodeFlowLayout1",
                                                "",
                                                "",
                                                "",
                                                "",
                                                parseMovurls(areaChildren[j], animeEpisodeDataBean)
                                            )
                                        )
                                    }
                                    "imgs" -> {
                                        playBeanDataList.add(
                                            AnimeDetailDataBean(
                                                "gridRecyclerView1",
                                                "", "", "", "", null,
                                                ParseHtmlUtil.parseImg(areaChildren[j])
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                playBean = PlayBean(
                    "", "", title, episode,
                    playBeanDataList
                )
                mldPlayBean.postValue(playBean)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getPlayData(partUrl)
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
        const val TAG = "HomeViewModel"
    }
}