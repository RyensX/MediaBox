package com.skyd.imomoe.model.impls.custom

import android.app.Activity
import android.view.View
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Api.Companion.MAIN_URL
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.util.html.source.GettingCallback
import com.skyd.imomoe.util.html.source.web.GettingUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.ref.SoftReference
import java.net.URLDecoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CustomPlayModel : IPlayModel {
    private var mActivity: SoftReference<Activity>? = null

    private fun getVideoUrl(iframeSrc: String, callback: GettingCallback) {
        val activity = mActivity?.get()
        if (activity == null || activity.isDestroyed) throw Exception("activity不存在或状态错误")
        activity.runOnUiThread {
            GettingUtil.instance.activity(activity)
                .url(iframeSrc).start(object : GettingCallback {
                    override fun onGettingSuccess(webView: View?, html: String) {
                        callback.onGettingSuccess(webView, html)
                    }

                    override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                        callback.onGettingError(webView, url, errorCode)
                    }

                })
        }
    }

    override suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> {
        var resultVideoUrl = false
        var resultData = false
        val playBeanDataList: ArrayList<IAnimeDetailBean> = ArrayList()
        val episodesList: ArrayList<AnimeEpisodeDataBean> = ArrayList()
        val title = AnimeTitleBean("", "", "")
        val episode =
            AnimeEpisodeDataBean(
                "", "",
                ""
            )
        val playBean = PlayBean("", "", title, episode, playBeanDataList)
        val url = MAIN_URL + partUrl
        val document = JsoupUtil.getDocument(url)
        return suspendCancellableCoroutine{ cancellableContinuation ->
            val activity = mActivity?.get()
            if (activity == null || activity.isDestroyed) throw Exception("activity不存在或状态错误")
            activity.runOnUiThread {
                GettingUtil.instance.activity(activity)
                    .url(url).start(object : GettingCallback {
                        override fun onGettingSuccess(webView: View?, html: String) {
                            GettingUtil.instance.release()
                            val document = Jsoup.parse(html)
                            val children: Elements = document.body().children()
                            for (i in children.indices) {
                                when (children[i].className()) {
                                    "play", "player" -> {
                                        var iframeSrc = children[i]
                                            .select("[class=bofang]")
                                            .select("iframe")[0].attr("src")
                                        if (iframeSrc.startsWith("/yxsf/player/ckx1")) {
                                            val videoUrl = URLDecoder.decode(
                                                iframeSrc.substringAfter("url=")
                                                    .substringBefore("&"), "UTF-8"
                                            )
                                            animeEpisodeDataBean.videoUrl = videoUrl
                                            resultVideoUrl = true
                                            if (resultData) cancellableContinuation.resume(
                                                Triple(
                                                    playBeanDataList,
                                                    episodesList,
                                                    playBean
                                                )
                                            )
                                            continue
                                        } else if (iframeSrc.startsWith("/")) iframeSrc =
                                            MAIN_URL + iframeSrc
                                        getVideoUrl(iframeSrc, object : GettingCallback {
                                            override fun onGettingSuccess(
                                                webView: View?, html: String
                                            ) {
                                                GettingUtil.instance.release()
                                                val iframe = Jsoup.parse(html)
                                                val videoUrl =
                                                    iframe.body().getElementById("video")!!
                                                        .select("video").attr("src")
                                                animeEpisodeDataBean.videoUrl = videoUrl
                                                resultVideoUrl = true
                                                if (resultData) cancellableContinuation.resume(
                                                    Triple(
                                                        playBeanDataList,
                                                        episodesList,
                                                        playBean
                                                    )
                                                )
                                            }

                                            override fun onGettingError(
                                                webView: View?, url: String?, errorCode: Int
                                            ) {
                                                GettingUtil.instance.release()
                                                cancellableContinuation.resumeWithException(
                                                    Exception("onGettingError,url:$url,errorCode:$errorCode")
                                                )
                                            }

                                        })

                                    }
                                }
                            }
                        }

                        override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                            GettingUtil.instance.release()
                            cancellableContinuation.resumeWithException(Exception("onGettingError,url:$url,errorCode:$errorCode"))
                        }

                    })
            }
            val children: Elements = document.allElements
            for (i in children.indices) {
                when (children[i].className()) {
                    "area" -> {
                        val areaChildren = children[i].children()
                        for (j in areaChildren.indices) {
                            when (areaChildren[j].className()) {
                                "gohome" -> {        //标题
                                    areaChildren[j].select("a").apply {
                                        title.title = get(size - 1).text()
                                        title.actionUrl = get(size - 1).attr("href")
                                    }
                                    episode.title = areaChildren[j].select("span")
                                        .text().replace("：", "")
                                    if (episode.title.startsWith(": ")) {
                                        episode.title = episode.title.replaceFirst(": ", "")
                                    }
                                    animeEpisodeDataBean.title = episode.title
                                }
                                "tabs" -> {
                                    val menu0 = areaChildren[j].select("[class=menu0]")[0]
                                    val main0 = areaChildren[j].select("[class=main0]")[0]
                                    val li = menu0.select("li")
                                    var movurl = main0.select("[class=movurl]")
                                    if (movurl.size == 0)
                                        movurl = main0.select("[class=movurl movurl_pan]")
                                    for (l: Int in li.indices) {
                                        if (movurl[l].select("ul").select("li").size == 0) continue
                                        playBeanDataList.add(
                                            AnimeDetailBean(
                                                Const.ViewHolderTypeString.HEADER_1, "",
                                                li[l].text(),
                                                "",
                                                null
                                            )
                                        )

                                        val list = CustomParseHtmlUtil.parseMovurls(
                                            movurl[l],
                                            animeEpisodeDataBean
                                        )
                                        episodesList.addAll(list)
                                        playBeanDataList.add(
                                            AnimeDetailBean(
                                                Const.ViewHolderTypeString.HORIZONTAL_RECYCLER_VIEW_1,
                                                "",
                                                "",
                                                "",
                                                list
                                            )
                                        )
                                    }
                                }
                                "botit" -> {
                                    playBeanDataList.add(
                                        AnimeDetailBean(
                                            Const.ViewHolderTypeString.HEADER_1,
                                            "",
                                            CustomParseHtmlUtil.parseBotit(areaChildren[j]),
                                            ""
                                        )
                                    )
                                }
                                "movurls" -> {      //集数列表
                                    episodesList.addAll(
                                        CustomParseHtmlUtil.parseMovurls(
                                            areaChildren[j],
                                            animeEpisodeDataBean
                                        )
                                    )
                                    playBeanDataList.add(
                                        AnimeDetailBean(
                                            Const.ViewHolderTypeString.HORIZONTAL_RECYCLER_VIEW_1,
                                            "",
                                            "",
                                            "",
                                            episodesList
                                        )
                                    )
                                }
                                "imgs" -> {
                                    playBeanDataList.addAll(
                                        CustomParseHtmlUtil.parseImg(areaChildren[j], url)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            playBean.title = title
            playBean.episode = episode
            playBean.data = playBeanDataList
            resultData = true
            if (resultVideoUrl) cancellableContinuation.resume(
                Triple(playBeanDataList, episodesList, playBean)
            )
        }
    }

    override fun clearActivity() {
        GettingUtil.instance.releaseAll()
        mActivity = null
    }

    override suspend fun refreshAnimeEpisodeData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Boolean = suspendCancellableCoroutine { cancellableContinuation ->
        val url = MAIN_URL + partUrl
        val activity = mActivity?.get()
        if (activity == null || activity.isDestroyed) throw Exception("activity不存在或状态错误")
        activity.runOnUiThread {
            GettingUtil.instance.activity(activity)
                .url(url).start(object : GettingCallback {
                    override fun onGettingSuccess(webView: View?, html: String) {
                        GettingUtil.instance.release()
                        val document = Jsoup.parse(html)
                        val children: Elements = document.body().children()
                        for (i in children.indices) {
                            when (children[i].className()) {
                                "player" -> {
                                    var iframeSrc = children[i]
                                        .select("[class=bofang]").select("iframe")[0].attr("src")
                                    if (iframeSrc.startsWith("/")) iframeSrc = MAIN_URL + iframeSrc
                                    getVideoUrl(iframeSrc, object : GettingCallback {
                                        override fun onGettingSuccess(
                                            webView: View?, html: String
                                        ) {
                                            GettingUtil.instance.release()
                                            val iframe = Jsoup.parse(html)
                                            val videoUrl = iframe.body().getElementById("video")!!
                                                .select("video").attr("src")
                                            animeEpisodeDataBean.videoUrl = videoUrl
                                            cancellableContinuation.resume(true)
                                        }

                                        override fun onGettingError(
                                            webView: View?, url: String?, errorCode: Int
                                        ) {
                                            GettingUtil.instance.release()
                                            cancellableContinuation.resumeWithException(Exception("onGettingError,url:$url,errorCode:$errorCode"))
                                        }

                                    })

                                }
                            }
                        }
                    }

                    override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                        GettingUtil.instance.release()
                        cancellableContinuation.resumeWithException(Exception("onGettingError,url:$url,errorCode:$errorCode"))
                    }

                })
        }
    }

    override suspend fun getAnimeCoverImageBean(detailPartUrl: String): ImageBean? {
        try {
            val url = MAIN_URL + detailPartUrl
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
                                when (fireLChildren[k].className()) {
                                    "thumb l" -> {
                                        return ImageBean(
                                            "", "", CustomParseHtmlUtil.getCoverUrl(
                                                fireLChildren[k].select("img").attr("src"),
                                                url
                                            ), url
                                        )
                                    }
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

    override fun setActivity(activity: Activity) {
        mActivity = SoftReference(activity)
    }

    override suspend fun getAnimeEpisodeUrlData(partUrl: String): String? =
        suspendCancellableCoroutine { cancellableContinuation ->
            val url = MAIN_URL + partUrl
            val activity = mActivity?.get()
            if (activity == null || activity.isDestroyed) throw Exception("activity不存在或状态错误")
            activity.runOnUiThread {
                GettingUtil.instance.activity(activity)
                    .url(url).start(object : GettingCallback {
                        override fun onGettingSuccess(webView: View?, html: String) {
                            GettingUtil.instance.release()
                            val document = Jsoup.parse(html)
                            val children: Elements = document.body().children()
                            for (i in children.indices) {
                                when (children[i].className()) {
                                    "player" -> {
                                        var iframeSrc = children[i]
                                            .select("[class=bofang]")
                                            .select("iframe")[0].attr("src")
                                        if (iframeSrc.startsWith("/")) iframeSrc =
                                            MAIN_URL + iframeSrc
                                        getVideoUrl(iframeSrc, object : GettingCallback {
                                            override fun onGettingSuccess(
                                                webView: View?, html: String
                                            ) {
                                                GettingUtil.instance.release()
                                                val iframe = Jsoup.parse(html)
                                                val videoUrl =
                                                    iframe.body().getElementById("video")!!
                                                        .select("video").attr("src")
                                                cancellableContinuation.resume(videoUrl)
                                            }

                                            override fun onGettingError(
                                                webView: View?, url: String?, errorCode: Int
                                            ) {
                                                GettingUtil.instance.release()
                                                cancellableContinuation.resumeWithException(
                                                    Exception("onGettingError,url:$url,errorCode:$errorCode")
                                                )
                                            }

                                        })

                                    }
                                }
                            }
                        }

                        override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                            GettingUtil.instance.release()
                            cancellableContinuation.resumeWithException(Exception("onGettingError,url:$url,errorCode:$errorCode"))
                        }

                    })
            }
        }

}