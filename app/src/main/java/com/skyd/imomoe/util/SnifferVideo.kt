package com.skyd.imomoe.util

import android.app.Activity
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.html.source.DefaultUICallback
import com.skyd.imomoe.util.html.source.SniffingUICallback
import com.skyd.imomoe.util.html.source.web.SniffingUtil
import org.jsoup.Jsoup


object SnifferVideo {
    private val sniffingUrlList: MutableList<String> by lazy { ArrayList() }
    private var serverApi: String = "https://yuan.cuan.la/barrage"
    private var serverKey: String = "mao"
    private var videoId: String = ""

    private fun getSrc(html: String, type: Int = 0): String {
        return when (type) {
            0 -> {
                Jsoup.parse(html).select("body")[0].select("[class=player]")[0]
                    .getElementById("playbox").select("iframe")[0]
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
                    .select("body")[0].getElementById("player")
                    .select("[class=leleplayer-video-wrap]")
                    .select("video").attr("src")
            }
        }
    }

    private fun getPlayerHtmlSource(
        activity: Activity,
        url: String,
        referer: String = "",
        listener: SniffingUICallback,
        type: Int = 0
    ) {
        activity.runOnUiThread {
            SniffingUtil.instance.activity(activity).referer(referer)
                .url(url)
                .start(object : DefaultUICallback() {
                    override fun onSniffingSuccess(webView: View?, html: String) {
                        val src = getSrc(html, type)
                        Log.i("getPlayerHtmlSource $type", html)
                        if (type == 2) {
                            listener.onSniffingSuccess(webView, src)
                            return
                        }
                        getPlayerHtmlSource(
                            activity,
                            src,
                            referer,
                            listener,
                            type + 1
                        )
                    }

                    override fun onSniffingStart(webView: View?, url: String?) {
                        listener.onSniffingStart(webView, url)
                    }

                    override fun onSniffingFinish(webView: View?, url: String?) {
                        listener.onSniffingFinish(webView, url)
                    }

                    override fun onSniffingError(webView: View?, url: String?, errorCode: Int) {
                        listener.onSniffingError(webView, url, errorCode)
                    }
                })
        }
    }

    fun getQzzVideoUrl(
        activity: Activity,
        partUrl: String,
        referer: String = "",
        callback: (url: String, danMuUrl: String) -> Unit
    ) {
        if (sniffingUrlList.contains(partUrl)) {
            activity.getString(R.string.getting_complex_video).showToast()
        }
        getPlayerHtmlSource(activity, Api.MAIN_URL + partUrl, Api.MAIN_URL + referer,
            object : SniffingUICallback {
                private lateinit var waitingDialog: MaterialDialog
                private var error = false
                override fun onSniffingFinish(webView: View?, url: String?) {
                    waitingDialog.message(
                        text = activity.getString(
                            R.string.get_complex_video_finished,
                            "\n${url}"
                        )
                    )
                }

                override fun onSniffingError(webView: View?, url: String?, errorCode: Int) {
                    if (error) return
                    error = true
                    activity.getString(R.string.getting_complex_video_failed, errorCode.toString())
                        .showToast()
                    sniffingUrlList.remove(partUrl)
                    waitingDialog.dismiss()
                }

                override fun onSniffingStart(webView: View?, url: String?) {
                    if (!this::waitingDialog.isInitialized) {
                        waitingDialog = showWaitingToSniffingDialog(activity)
                        sniffingUrlList.add(partUrl)
                    }
                    waitingDialog.message(
                        text = activity.getString(
                            R.string.start_get_complex_video,
                            "\n${url}"
                        )
                    )
                }

                override fun onSniffingSuccess(webView: View?, html: String) {
                    activity.runOnUiThread {
                        SniffingUtil.instance.releaseWebView()
                    }
                    sniffingUrlList.remove(partUrl)
                    waitingDialog.dismiss()
                    Log.i("getQzzVideoUrl", html)
                    callback(html, "https:$serverApi/barrage/api?ac=dm&key=$serverKey&id=$videoId")
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
        callback: (url: String, danMuUrl: String) -> Unit
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

    private fun showWaitingToSniffingDialog(activity: Activity): MaterialDialog {
        return MaterialDialog(activity).show {
            title(res = R.string.get_complex_videos)
            message(text = activity.getString(R.string.getting_complex_video, ""))
            cancelOnTouchOutside(false)
            cancelable(false)
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