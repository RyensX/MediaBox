package com.skyd.imomoe.util

import com.skyd.imomoe.bean.AnimeAreaBean
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.AnimeTypeBean
import com.skyd.imomoe.config.Api
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.ArrayList

object ParseHtmlUtil {
    fun parseTopli(
        element: Element,
        type: String = "animeCover5"
    ): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            var url = elements[i].select("a")[1].attr("href")
            var title = elements[i].select("a")[1].text()
            if (elements[i].select("a").size == 2) {
                url = elements[i].select("a")[0].attr("href")
                title = elements[i].select("a")[0].text()
            }
            val areaUrl = elements[i].select("span").select("a")
                .attr("href")
            val areaTitle = elements[i].select("span").select("a").text()
            var episodeUrl = elements[i].select("b").select("a")
                .attr("href")
            val episodeTitle = elements[i].select("b").select("a").text()
            val date = elements[i].select("em").text()
            if (episodeUrl == "") {
                episodeUrl = url
            }
            animeShowList.add(
                AnimeCoverBean(
                    type, url, Api.MAIN_URL + url,
                    title, "", "", null, null,
                    AnimeEpisodeDataBean("", episodeUrl, episodeTitle),
                    AnimeAreaBean("", areaUrl, Api.MAIN_URL + areaUrl, areaTitle),
                    date
                )
            )
        }
        return animeShowList
    }

    fun parseDnews(
        element: Element,
        type: String = "animeCover4"
    ): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            if (cover.startsWith("/")) {
                //url不全的情况
                cover = Api.MAIN_URL + cover
            }
            val title = elements[i].select("p").select("a").text()
            animeShowList.add(
                AnimeCoverBean(
                    type, url, Api.MAIN_URL + url,
                    title, cover, ""
                )
            )
        }
        return animeShowList
    }

    fun parseLpic(
        element: Element,
        type: String = "animeCover3"
    ): List<AnimeCoverBean> {
        val animeCover3List: MutableList<AnimeCoverBean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            val cover = results[i].select("a")
                .select("img").attr("src")
            val title = results[i].select("h2")
                .select("a").attr("title")
            val url = results[i].select("h2")
                .select("a").attr("href")
            val episode = results[i].select("span")
                .select("font").text()
            val types = results[i].select("span")[1].select("a")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                animeType.add(
                    AnimeTypeBean(
                        type, types[j].attr("href"),
                        Api.MAIN_URL + types[j].attr("href"),
                        types[j].text()
                    )
                )
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCoverBean(
                    "animeCover3",
                    url, Api.MAIN_URL + url, title, cover, episode, animeType, describe
                )
            )
        }
        return animeCover3List
    }

    fun parseDtit(
        element: Element,
    ): String {
        return element.children()[0].text()
    }

    fun parseBotit(
        element: Element,
    ): String {
        return element.select("h2").text()
    }

    fun parseMovurls(
        element: Element,
        selected: AnimeEpisodeDataBean?,
        type: String = "animeEpisode1"
    ): List<AnimeEpisodeDataBean> {
        val animeEpisodeList: MutableList<AnimeEpisodeDataBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (k in elements.indices) {
            if (selected != null && elements[k].className() == "sel") {
                selected.title = elements[k].select("a").text()
                selected.actionUrl = elements[k].select("a").attr("href")
            }
            animeEpisodeList.add(
                AnimeEpisodeDataBean(
                    type,
                    elements[k].select("a").attr("href"),
                    elements[k].select("a").text()
                )
            )
        }
        return animeEpisodeList
    }

    fun parseImg(element: Element, type: String = "animeCover1"): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            if (cover.startsWith("/")) {
                //url不全的情况
                cover = Api.MAIN_URL + cover
            }
            val title = elements[i].select("[class=tname]").select("a").text()
            var episode = ""
            if (elements[i].select("p").size > 1) {
                episode = elements[i].select("p")[1].select("a").text()
            }
            animeShowList.add(
                AnimeCoverBean(
                    type, url, Api.MAIN_URL + url,
                    title, cover, episode
                )
            )
        }
        return animeShowList
    }
}