package com.skyd.imomoe.model.impls.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.model.interfaces.IRouteProcessor
import com.skyd.imomoe.util.Util.getSubString
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.activity.*
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder


class CustomRouteProcessor : IRouteProcessor {
    override fun process(context: Context, actionUrl: String): Boolean {
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = CustomConst()
        var solved = true
        when {
            decodeUrl.startsWith(const.actionUrl.ANIME_DETAIL()) -> {     //番剧封面点击进入
                context.startActivity(
                    Intent(context, AnimeDetailActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_PLAY()) -> {     //番剧每一集点击进入
                val playCode = actionUrl.getSubString("\\/vp\\/", "\\.")[0].split("-")
                if (playCode.size >= 2) {
                    var detailPartUrl =
                        actionUrl.substringAfter(const.actionUrl.ANIME_DETAIL(), "")
//                    if (detailPartUrl.isBlank()) App.context.getString(R.string.error_play_episode).showToast()
                    detailPartUrl = const.actionUrl.ANIME_DETAIL() + detailPartUrl
                    context.startActivity(
                        Intent(context, PlayActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(
                                "partUrl",
                                actionUrl.substringBefore(const.actionUrl.ANIME_DETAIL())
                            )
                            .putExtra("detailPartUrl", detailPartUrl)
                    )
                } else {
                    "播放集数解析错误！".showToast()
                }
            }
        }
        return solved
    }

    override fun process(activity: Activity, actionUrl: String): Boolean {
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = CustomConst()
        var solved = true
        when {
            decodeUrl.startsWith(com.skyd.imomoe.config.Const.ActionUrl.ANIME_CLASSIFY) -> {     //如进入分类页面
                val paramList =
                    actionUrl.replace(com.skyd.imomoe.config.Const.ActionUrl.ANIME_CLASSIFY, "")
                        .split("/")
                if (paramList.size == 4) {      //例如  /list/?label=恋爱/恋爱  分割后是3个参数：""，list，?label=恋爱，恋爱
                    activity.startActivity(
                        Intent(activity, ClassifyActivity::class.java)
                            .putExtra("partUrl", "/${paramList[1]}/${paramList[2]}")
                            .putExtra("classifyTabTitle", "")
                            .putExtra("classifyTitle", paramList[3])
                    )
                } else "跳转协议格式错误".showToast()
            }
            decodeUrl.matches(Regex(".*year=[0-9]*.*")) &&
                    decodeUrl.matches(Regex(".*season=[0-9]*.*")) -> {     //如https://www.yhdmp.net/list/?year=2021&season=7新番列表
                activity.startActivity(
                    Intent(activity, MonthAnimeActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_RANK()) -> {     // 排行榜
                activity.startActivity(Intent(activity, RankActivity::class.java))
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_SEARCH()) -> {     // 进入搜索页面
                val paramMap: HashMap<String, String> = HashMap()
                try {
                    URL(const.MAIN_URL() + actionUrl).query?.let { query ->
                        query.split("&").forEach { kv ->
                            kv.split("=").let { v ->
                                paramMap[v[0]] = v[1]
                            }
                        }
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                decodeUrl.replace(const.actionUrl.ANIME_SEARCH(), "").let {
                    val keyWord: String = paramMap["kw"] ?: ""
                    activity.startActivity(
                        Intent(activity, SearchActivity::class.java)
                            .putExtra("keyWord", keyWord)
                            .putExtra("pageNumber", actionUrl)
                    )
                }
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_DETAIL()) -> {     //番剧封面点击进入
                activity.startActivity(
                    Intent(activity, AnimeDetailActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_PLAY()) -> {     //番剧每一集点击进入
                val playCode = actionUrl.getSubString("\\/vp\\/", "\\.")[0].split("-")
                if (playCode.size >= 2) {
                    var detailPartUrl =
                        actionUrl.substringAfter(const.actionUrl.ANIME_DETAIL(), "")
//                    if (detailPartUrl.isBlank()) App.context.getString(R.string.error_play_episode).showToast()
                    detailPartUrl = const.actionUrl.ANIME_DETAIL() + detailPartUrl
                    activity.startActivity(
                        Intent(activity, PlayActivity::class.java)
                            .putExtra(
                                "partUrl",
                                actionUrl.substringBefore(const.actionUrl.ANIME_DETAIL())
                            )
                            .putExtra("detailPartUrl", detailPartUrl)
                    )
                } else {
                    "播放集数解析错误！".showToast()
                }
            }
            else -> {
                const.actionUrl.let { actionUrlClass ->
                    if (actionUrlClass is CustomConst.ActionUrl) {
                        when {
                            decodeUrl.startsWith(actionUrlClass.ANIME_LINK()) -> {     // 链接
                                "暂不支持带有referer的外部浏览器跳转".showToast()
//                                    activity.startActivity(
//                                        Intent(activity, WebViewActivity::class.java)
//                                            .putExtra("url", const.MAIN_URL() + decodeUrl)
//                                            .putExtra(
//                                                "headers", hashMapOf(
//                                                    "Referer" to const.MAIN_URL().toString(),
//                                                    "User-Agent" to Const.Request.USER_AGENT_ARRAY[Random.nextInt(
//                                                        Const.Request.USER_AGENT_ARRAY.size
//                                                    )]
//                                                )
//                                            )
//                                    )
                            }
                            else -> solved = false
                        }
                    } else {
                        solved = false
                    }
                }
            }
        }
        return solved
    }
}
