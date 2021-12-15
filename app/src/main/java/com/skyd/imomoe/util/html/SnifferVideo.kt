package com.skyd.imomoe.util.html

import android.app.Activity
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.html.source.DefaultUICallback
import com.skyd.imomoe.util.html.source.GettingUICallback
import com.skyd.imomoe.util.html.source.web.GettingUtil
import org.jsoup.Jsoup
import kotlin.jvm.Throws


object SnifferVideo {
    const val PARSE_URL_ERROR = -100
    const val KEY = "key"
    const val AC = "ac"
    const val VIDEO_ID = "id"
    const val SERVER_API = "api"
    const val DANMU_URL = "danmuUrl"
    const val REFEREER_URL = "referer"
    private val sniffingUrlList: MutableList<String> by lazy { ArrayList() }
    private var serverApi: String = "https://yuan.cuan.la/barrage"
    private var serverKey: String = "mao"
    private var videoId: String = ""
    private var referer: String = "http://tup.yhdm.so/"

    @Throws(IndexOutOfBoundsException::class)
    private fun getSrc(html: String, type: Int = 0): String {
        return when (type) {
            0 -> {
                Jsoup.parse(html).select("body")[0].select("[class=player]")[0]
                    .getElementById("playbox")!!.select("iframe")[0]
                    .attr("src")
            }
            1 -> {
                Jsoup.parse(html).select("body")[0]
                    .select("iframe")[0]
                    .attr("src")
            }
            else -> {
                val script = Jsoup.parse(html).select("body")[0].select("script")[0].toString()
                val line = script.split("\n")
                line.forEach {
                    val l = it.trim()
                    when {
                        l.contains("\"ServerApi\": ") -> {
                            serverApi = l.replace("\"ServerApi\": \"", "")
                                .replace(Regex("\",.*"), "")
                        }
                        l.contains("\"ServerKey\": ") -> {
                            serverKey = l.replace("\"ServerKey\": \"", "")
                                .replace(Regex("\",.*"), "")
                        }
                        l.contains("\"id\": ") -> {
                            videoId = l.replace("\"id\": \"", "")
                                .replace(Regex("\",.*"), "")
                        }
                    }
                }
                Jsoup.parse(html)
                    .select("body")[0].getElementById("player")!!
                    .select("[class=leleplayer-video-wrap]")
                    .select("video").attr("src")
            }
        }
    }

    private fun getPlayerHtmlSource(
        activity: Activity,
        url: String,
        referer: String = "",
        listener: GettingUICallback,
        type: Int = 0
    ) {
        if (type > 2) return
        if (type == 1) this.referer = url
        activity.runOnUiThread {
            GettingUtil.instance.activity(activity).referer(referer)
                .url(url)
                .start(object : DefaultUICallback() {
                    override fun onGettingSuccess(webView: View?, html: String) {
                        val src = try {
                            getSrc(html, type)
                        } catch (e: IndexOutOfBoundsException) {
                            // 解析地址出现错误
                            e.printStackTrace()
                            Log.e("getSrc IOOBException", html)
                            onGettingError(webView, url, PARSE_URL_ERROR)
                            return
                        }

                        Log.i("getPlayerHtmlSource $type", html)
                        if (type == 2) {
                            if (src.startsWith("blob:"))
                                "HTML5 blob格式资源".showToast()
                            else
                                listener.onGettingSuccess(webView, src)
                            return
                        }
                        getPlayerHtmlSource(activity, src, referer, listener, type + 1)
                    }

                    override fun onGettingStart(webView: View?, url: String?) {
                        listener.onGettingStart(webView, url)
                    }

                    override fun onGettingFinish(webView: View?, url: String?) {
                        listener.onGettingFinish(webView, url)
                    }

                    override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                        listener.onGettingError(webView, url, errorCode)
                    }
                })
        }
    }

    fun getQzzVideoUrl(
        activity: Activity,
        partUrl: String,
        referer: String = "",
        callback: (url: String, paramMap: HashMap<String, String>) -> Unit
    ) {
        if (sniffingUrlList.contains(partUrl)) {
            activity.getString(R.string.getting_complex_video).showToast()
        } else {
            sniffingUrlList.add(partUrl)
        }
        getPlayerHtmlSource(activity,
            Api.MAIN_URL + partUrl,
            Api.MAIN_URL + referer,
            object : GettingUICallback {
                private lateinit var waitingDialog: MaterialDialog
                private var error = false
                override fun onGettingFinish(webView: View?, url: String?) {
                    waitingDialog.message(
                        text = activity.getString(
                            R.string.get_complex_video_finished,
                            "\n${url}"
                        )
                    )
                }

                override fun onGettingError(webView: View?, url: String?, errorCode: Int) {
                    activity.runOnUiThread { GettingUtil.instance.releaseWebView() }
                    if (error) return
                    error = true
                    when (errorCode) {
                        PARSE_URL_ERROR -> {
                            activity.getString(R.string.fail_to_parse_page_url).showToast()
                        }
                        else -> {
                            activity.getString(
                                R.string.getting_complex_video_failed, errorCode.toString()
                            ).showToast()
                        }
                    }
                    sniffingUrlList.remove(partUrl)
                    waitingDialog.dismiss()
                }

                override fun onGettingStart(webView: View?, url: String?) {
                    if (!this::waitingDialog.isInitialized) {
                        waitingDialog = showWaitingToSniffingDialog(activity, partUrl)
                    }
                    waitingDialog.message(
                        text = activity.getString(
                            R.string.start_get_complex_video,
                            "\n${url}"
                        )
                    )
                }

                override fun onGettingSuccess(webView: View?, html: String) {
                    activity.runOnUiThread {
                        GettingUtil.instance.releaseWebView()
                    }
                    sniffingUrlList.remove(partUrl)
                    waitingDialog.dismiss()
                    Log.i("getQzzVideoUrl", html)
                    HashMap<String, String>().apply {
                        put(KEY, serverKey)
                        put(AC, "dm")
                        put(VIDEO_ID, videoId)
                        put(SERVER_API, serverApi)
                        put(REFEREER_URL, this@SnifferVideo.referer)
                        put(
                            DANMU_URL,
                            "$serverApi/barrage/api?ac=dm&key=$serverKey&id=$videoId".run {
                                if (this.startsWith("http")) this
                                else "https:" + this
                            }
                        )
                        callback(html, this)
                    }
//                            showChooseVideoUrlDialog(activity, html, callback)
                }
            }
        )
    }

    fun askSnifferDialog(
        activity: Activity,
        title: String,
        message: String,
        partUrl: String,
        referer: String = "",
        callback: (url: String, paramMap: HashMap<String, String>) -> Unit
    ) {
        MaterialDialog(activity).show {
            title(text = title)
            message(text = message)
            positiveButton(text = "嗅探") {
                if (sniffingUrlList.contains(partUrl)) {
                    dismiss()
                }
                getQzzVideoUrl(activity, partUrl, referer, callback)
            }
            negativeButton(text = "取消") {
                dismiss()
            }
        }
    }

    private fun showWaitingToSniffingDialog(activity: Activity, partUrl: String): MaterialDialog {
        return MaterialDialog(activity).show {
            title(res = R.string.get_complex_videos)
            message(text = activity.getString(R.string.getting_complex_video))
            cancelOnTouchOutside(false)
            onCancel {
                GettingUtil.instance.releaseWebView()
                sniffingUrlList.remove(partUrl)
                activity.getString(R.string.cancel_to_get_video_url).showToast()
            }
        }
    }

//    private fun showChooseVideoUrlDialog(
//        activity: Activity,
//        html: String,
//        callback: (
//            dialog: MaterialDialog, index: Int,
//            text: CharSequence, videos: MutableList<String>
//        ) -> Unit
//    ): MaterialDialog {
//        val removedDuplicateVideosList = removeDuplicateUrls(html)
//        val list: MutableList<CharSequence> = removedDuplicateVideosList.run {
//            val l: MutableList<CharSequence> = ArrayList()
//            forEach {
//                l.add(it.url.let { url ->
//                    if (url.length > 70) "${url.substring(0, 70)}…"
//                    else url
//                })
//            }
//            l
//        }
//        return MaterialDialog(activity).show {
//            title(
//                text = activity.getString(
//                    R.string.please_choose_a_video_link, list.size.toString()
//                )
//            )
//            listItems(items = list) { dialog, index, text ->
//                callback(dialog, index, text, removedDuplicateVideosList)
////                videoPlayer.startPlay(videos[index].url, viewModel.animeEpisodeDataBean.title)
//                dialog.dismiss()
//            }
//            negativeButton { dismiss() }
//        }
//    }


//    private fun removeDuplicateUrls(list: List<SniffingVideo>): MutableList<SniffingVideo> {
//        val videos: MutableList<SniffingVideo> = ArrayList()
//        val urls: MutableList<String> = ArrayList()
//        list.forEach { sniffingVideo ->
//            if (!urls.contains(sniffingVideo.url) &&
//                !sniffingVideo.url.startsWith("mp4")
//            ) {
//                urls.add(sniffingVideo.url)
//                videos.add(sniffingVideo)
//            }
//        }
//        return videos
//    }
}