package com.skyd.imomoe.model.impls

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.interfaces.IRouterProcessor
import com.skyd.imomoe.util.Util.getSubString
import com.skyd.imomoe.util.Util.isYearMonth
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.activity.*
import java.net.URLDecoder

class RouterProcessor : IRouterProcessor {
    override fun process(context: Context?, actionUrl: String?): Boolean {
        context ?: return false
        actionUrl ?: return false
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = DataSourceManager.getConst() ?: Const()
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

    override fun process(activity: Activity?, actionUrl: String?): Boolean {
        activity ?: return false
        actionUrl ?: return false
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val const = DataSourceManager.getConst() ?: Const()
        var solved = true
        when {
            decodeUrl.replace("/", "").isYearMonth() -> {     //如201907月新番列表
                activity.startActivity(
                    Intent(activity, MonthAnimeActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(const.actionUrl.ANIME_TOP()) -> {     // 排行榜
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
