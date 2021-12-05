package com.skyd.imomoe.model.impls.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.model.interfaces.IRouteProcessor
import com.skyd.imomoe.util.Util.getSubString
import com.skyd.imomoe.util.Util.isYearMonth
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.activity.*
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
                val playCode = actionUrl.getSubString("\\/v\\/", "\\.")[0].split("-")
                if (playCode.size >= 2) {
                    var detailPartUrl = actionUrl.substringAfter(const.actionUrl.ANIME_DETAIL(), "")
//            if (detailPartUrl.isBlank()) App.context.getString(R.string.error_play_episode).showToast()
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
                    App.context.getString(R.string.error_play_episode).showToast()
                }
            }
            else -> solved = false
        }
        return solved
    }

    override fun process(activity: Activity, actionUrl: String): Boolean {
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = CustomConst()
        var solved = true
        when {
            decodeUrl.startsWith(com.skyd.imomoe.config.Const.ActionUrl.ANIME_CLASSIFY) -> {     //如进入分类页面
                val paramList = actionUrl.replace(com.skyd.imomoe.config.Const.ActionUrl.ANIME_CLASSIFY, "").split("/")
                if (paramList.size == 3) {      //例如  /japan/日本  分割后是3个参数：""，japan，日本
                    activity.startActivity(
                        Intent(activity, ClassifyActivity::class.java)
                            .putExtra("partUrl", "/" + paramList[1] + "/")
                            .putExtra("classifyTabTitle", "")
                            .putExtra("classifyTitle", paramList[2])
                    )
                } else App.context.resources.getString(R.string.action_url_format_error)
                    .showToast()
            }
            decodeUrl.replace("/", "").isYearMonth() -> {     //如201907月新番列表
                activity.startActivity(
                    Intent(activity, MonthAnimeActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_RANK()) -> {     // 排行榜
                activity.startActivity(Intent(activity, RankActivity::class.java))
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_SEARCH()) -> {     // 进入搜索页面
                decodeUrl.replace(const.actionUrl.ANIME_SEARCH(), "").let {
                    val keyWord = it.replaceFirst(Regex("/.*"), "")
                    val pageNumber = it.replaceFirst(Regex("($keyWord/)|($keyWord)"), "")
                    activity.startActivity(
                        Intent(activity, SearchActivity::class.java)
                            .putExtra("keyWord", keyWord)
                            .putExtra("pageNumber", pageNumber)
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
                val playCode = actionUrl.getSubString("\\/v\\/", "\\.")[0].split("-")
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
                    App.context.getString(R.string.error_play_episode).showToast()
                }
            }
            else -> solved = false
        }
        return solved
    }
}
