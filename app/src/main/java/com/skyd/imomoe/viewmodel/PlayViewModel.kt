package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.JsoupUtil
import com.skyd.imomoe.util.ParseHtmlUtil
import com.skyd.imomoe.util.ParseHtmlUtil.parseBotit
import com.skyd.imomoe.util.ParseHtmlUtil.parseMovurls
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*
import kotlin.jvm.Throws


class PlayViewModel : ViewModel() {
    var playBean: PlayBean? = null
    var partUrl: String = ""
    var animeCover: ImageBean = ImageBean("", "", "", "")
    var mldAnimeCover: MutableLiveData<Boolean> = MutableLiveData()
    var mldPlayBean: MutableLiveData<PlayBean> = MutableLiveData()
    var playBeanDataList: MutableList<AnimeDetailBean> = ArrayList()
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    val mldEpisodesList: MutableLiveData<Boolean> = MutableLiveData()
    val animeEpisodeDataBean = AnimeEpisodeDataBean("animeEpisode1", "", "")
    val mldAnimeEpisodeDataRefreshed: MutableLiveData<Boolean> = MutableLiveData()
    val mldGetAnimeEpisodeData: MutableLiveData<Int> = MutableLiveData()

    fun refreshAnimeEpisodeData(partUrl: String, title: String = "") {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                val document = JsoupUtil.getDocument(Api.MAIN_URL + partUrl)
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
        }
    }

    fun getAnimeEpisodeData(partUrl: String, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                val document = JsoupUtil.getDocument(Api.MAIN_URL + partUrl)
                val children: Elements = document.select("body")[0].children()
                for (i in children.indices) {
                    when (children[i].className()) {
                        "play" -> {
                            episodesList[position].videoUrl = children[i].select("[class=area]")
                                .select("[class=bofang]").select("div").attr("data-vid")
                                .replace("\$mp4", "")
                            break
                        }
                    }
                }
                mldEpisodesList.postValue(true)
                mldGetAnimeEpisodeData.postValue(position)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    fun getPlayData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                val title = AnimeTitleBean("", "", "")
                val episode =
                    AnimeEpisodeDataBean(
                        "", "",
                        ""
                    )
                val url = Api.MAIN_URL + partUrl
                val document = JsoupUtil.getDocument(url)
                val children: Elements = document.allElements
                playBeanDataList.clear()
                episodesList.clear()
                for (i in children.indices) {
                    when (children[i].className()) {
                        "play" -> {
                            val rawUrl = children[i].select("[class=area]")
                                .select("[class=bofang]").select("div").attr("data-vid")
                            animeEpisodeDataBean.videoUrl = when {
                                rawUrl.endsWith("\$mp4", true) -> rawUrl.replace("\$mp4", "")
                                rawUrl.endsWith("\$url", true) -> rawUrl.replace("\$url", "")
                                else -> ""
                            }
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
                                            AnimeDetailBean(
                                                "header1",
                                                "",
                                                parseBotit(areaChildren[j]),
                                                ""
                                            )
                                        )
                                    }
                                    "movurls" -> {      //集数列表
                                        episodesList.addAll(
                                            parseMovurls(
                                                areaChildren[j],
                                                animeEpisodeDataBean,
                                                "animeEpisode2"
                                            )
                                        )
                                        playBeanDataList.add(
                                            AnimeDetailBean(
                                                "horizontalRecyclerView1",
                                                "",
                                                "",
                                                "",
                                                episodesList
                                            )
                                        )
                                    }
                                    "imgs" -> {
                                        playBeanDataList.add(
                                            AnimeDetailBean(
                                                "gridRecyclerView1",
                                                "", "", "",
                                                animeCoverList = ParseHtmlUtil.parseImg(
                                                    areaChildren[j],
                                                    url
                                                )
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
                mldEpisodesList.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    // 更新追番集数数据
    fun updateFavoriteData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        lastEpisode: String,
        time: Long
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val favoriteAnimeDao = getAppDataBase().favoriteAnimeDao()
                val favoriteAnimeBean = favoriteAnimeDao.getFavoriteAnime(detailPartUrl)
                if (favoriteAnimeBean != null) {
                    favoriteAnimeBean.lastEpisode = lastEpisode
                    favoriteAnimeBean.lastEpisodeUrl = lastEpisodeUrl
                    favoriteAnimeBean.time = time
                    favoriteAnimeDao.updateFavoriteAnime(favoriteAnimeBean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 插入观看历史记录
    fun insertHistoryData(
        detailPartUrl: String,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val cover = if (animeCover.url.isBlank()) {
                    getAnimeCoverImageBean(detailPartUrl)
                        ?: return@launch
                } else animeCover
                getAppDataBase().historyDao().insertHistory(
                    HistoryBean(
                        "animeCover9", "", detailPartUrl,
                        playBean?.title?.title ?: "",
                        System.currentTimeMillis(),
                        cover,
                        partUrl,
                        animeEpisodeDataBean.title
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAnimeCover(detailPartUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bean = getAnimeCoverImageBean(detailPartUrl)
                    ?: throw Exception("null object, 无法获取CoverImageBean")
                animeCover.url = bean.url
                animeCover.referer = bean.referer
                mldAnimeCover.postValue(true)
            } catch (e: Exception) {
                mldAnimeCover.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    fun getAnimeCoverImageBean(detailPartUrl: String): ImageBean? {
        try {
            val url = Api.MAIN_URL + detailPartUrl
            val document = JsoupUtil.getDocument(url)
            //番剧头部信息
            val area: Elements = document.getElementsByClass("area")
            for (i in area.indices) {
                val areaChildren = area[i].children()
                for (j in areaChildren.indices) {
                    when (areaChildren[j].className()) {
                        "fire l" -> {
                            val fireLChildren =
                                areaChildren[j].select("[class=fire l]")[0].children()
                            for (k in fireLChildren.indices) {
                                if (fireLChildren[k].className() == "thumb l") {
                                    return ImageBean(
                                        "", "", fireLChildren[k]
                                            .select("img").attr("src"), url
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        const val TAG = "PlayViewModel"
    }
}