package com.su.mediabox.plugin

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.widget.Toast
import com.su.mediabox.pluginapi.AppUtil
import com.su.mediabox.util.Util
import com.su.mediabox.util.showToast
import com.su.mediabox.view.activity.*
import com.su.mediabox.pluginapi.Constant.ActionUrl
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import java.lang.ref.WeakReference
import java.net.URLDecoder

object AppRouteProcessor :
    AppUtil.IRouteProcessor {

    private val activityPool by lazy(LazyThreadSafetyMode.NONE) { mutableMapOf<Int, WeakReference<Activity>>() }

    var currentActivity: WeakReference<Activity>? = null
        private set

    fun init(activity: Activity) {
        if (currentActivity == null)
            updateTarget(activity)
    }

    fun updateTarget(activity: Activity) {
        val hashCode = activity.hashCode()
        currentActivity =
            activityPool[hashCode] ?: WeakReference(activity).also {
                activityPool[hashCode] = it
            }
    }

    private inline fun matchAndGetParams(
        actionUrl: String,
        action: String,
        matchSuccess: (List<String>) -> Unit
    ) =
        try {
            if (actionUrl.startsWith(action)) {
                actionUrl.removePrefix(action)
                    .removePrefix("/")
                    .removeSuffix("/")
                    .split("/")
                    .also { list ->
                        //可能部分路由需要把actionUrl作为参数，所以在判断和分割参数后再解码
                        matchSuccess(list.map { URLDecoder.decode(it, "UTF-8") })
                    }
                true
            } else false
        } catch (e: Exception) {
            Log.d("路由错误", e.message ?: "")
            e.printStackTrace()
            false
        }

    /**
     * 路由处理
     *
     * 必须以[ActionUrl]内常量开头加上参数，如:/action/param1/param2 (参数必须经过单独编码)
     * @return 路由成功与否
     */
    override fun process(actionUrl: String): Boolean {
        if (actionUrl.isEmpty())
            return false

        val activity: Activity = currentActivity?.get() ?: return false
        //val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")

        return when {
            //打开搜索页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_SEARCH) {
                activity.startActivity(
                    Intent(activity, SearchActivity::class.java).apply {
                        runCatching {
                            putExtra("keyWord", it[0])
                            putExtra("pageNumber", it[1])
                        }
                    }
                )
            } -> true
            //打开番剧详情页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_DETAIL) {
                activity.startActivity(
                    Intent(activity, VideoDetailActivity::class.java)
                        .putExtra("partUrl", it[0])
                )
            } -> true
            //打开播放页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_PLAY) {
                //参数：剧集信息/[封面]/[详情]
                activity.startActivity(
                    Intent(activity, PlayActivity::class.java).apply {
                        //必填的剧集信息
                        putExtra(PlayActivity.INTENT_EPISODE, it[0])
                        //可选（在详情页打开时由Activity主动提供）
                        //封面
                        it.getOrNull(1)?.also { putExtra(PlayActivity.INTENT_COVER, it) }
                        //详情链接
                        it.getOrNull(2)?.also { putExtra(PlayActivity.INTENT_DPU, it) }
                    }
                )
            } -> true
            //打开排行榜页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_RANK) {
                activity.startActivity(Intent(activity, RankActivity::class.java))
            } -> true
            //打开月新番页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_MONTH_NEW_ANIME) {
                activity.startActivity(
                    Intent(activity, MonthAnimeActivity::class.java)
                        .putExtra("partUrl", it[0])
                )
            } -> true
            //FIX_TODO 2022/2/13 12:49 0 目前分类页面还不可传递参数（即通过标签等打开），等待改进路由系统
            //打开分类页面
            matchAndGetParams(actionUrl, ActionUrl.ANIME_CLASSIFY) {
                activity.startActivity(
                    Intent(activity, ClassifyActivity::class.java)
                        //具体分类的链接
                        .putExtra("partUrl", it[0])
                        //如地区
                        .putExtra("classifyTabTitle", it.getOrNull(1) ?: "")
                        //如大陆
                        .putExtra("classifyTitle", it.getOrNull(2) ?: "")
                )
            } -> true
            //打开浏览器
            matchAndGetParams(actionUrl, ActionUrl.ANIME_BROWSER) {
                Util.openBrowser(it[0])
            } -> true
            //打开缓存列表
            matchAndGetParams(actionUrl, ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE) {
                activity.startActivity(
                    Intent(activity, AnimeDownloadActivity::class.java)
                        .putExtra("mode", it[0].toInt())
                        .putExtra("actionBarTitle", it[1])
                        .putExtra("directoryName", it[2])
                )
            } -> true
            //播放本地缓存
            matchAndGetParams(actionUrl, ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY) {
                //参数：URL/标题
                activity.startActivity(
                    Intent(activity, SimplePlayActivity::class.java)
                        .putExtra(SimplePlayActivity.URL, it[0])
                        .putExtra(SimplePlayActivity.TITLE, it[1])
                )
            } -> true
            matchAndGetParams(actionUrl, ActionUrl.ANIME_ANIME_DOWNLOAD_M3U8) {
                "暂不支持m3u8格式".showToast(Toast.LENGTH_LONG)
            } -> true
            //打开Activity
            matchAndGetParams(actionUrl, ActionUrl.ANIME_LAUNCH_ACTIVITY) {
                val cls = Class.forName(it[0])
                activity.startActivity(Intent(activity, cls).addFlags(FLAG_ACTIVITY_NEW_TASK))
            } -> true
            //未知路由
            else -> {
                "未知路由，请联系插件作者解决".showToast()
                false
            }
        }
    }
}