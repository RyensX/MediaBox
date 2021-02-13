package com.skyd.imomoe.view.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nex3z.flowlayout.FlowLayout
import com.shuyu.gsyvideoplayer.GSYVideoADManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper
import com.skyd.imomoe.view.adapter.PlayAdapter
import com.skyd.imomoe.viewmodel.PlayViewModel
import kotlinx.android.synthetic.main.activity_play.*


class PlayActivity : BaseActivity() {
    private var partUrl: String = ""
    private lateinit var viewModel: PlayViewModel
    private lateinit var adapter: PlayAdapter
    private var orientationUtils: OrientationUtils? = null
    private var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        setColorStatusBar(window, Color.BLACK)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        adapter = PlayAdapter(this, viewModel.playBeanDataList)

        orientationUtils = OrientationUtils(this, avp_play_activity)

        avp_play_activity.backButton?.setOnClickListener { finish() }
        avp_play_activity.getDownloadButton()?.setOnClickListener {
            getSheetDialog().show()
        }

        partUrl = intent.getStringExtra("partUrl") ?: ""

        val layoutManager = LinearLayoutManager(this)
        rv_play_activity.layoutManager = layoutManager
        rv_play_activity.setHasFixedSize(true)
        rv_play_activity.adapter = adapter

        srl_play_activity.setOnRefreshListener { viewModel.getPlayData(partUrl) }
        srl_play_activity.setColorSchemeResources(R.color.main_color)

        viewModel.mldPlayBean.observe(this, {
            srl_play_activity.isRefreshing = false

            tv_play_activity_title.text = viewModel.playBean?.title?.title

            adapter.notifyDataSetChanged()

            if (isFirstTime) {
                avp_play_activity.startPlay()
                isFirstTime = false
            }
        })

        //缓存番剧调用getAnimeEpisodeData()来获取视频url
        viewModel.mldGetAnimeEpisodeData.observe(this, {
            val url = viewModel.episodesList[it].videoUrl
            AnimeDownloadHelper.instance.downloadAnime(
                this,
                url,
                viewModel.playBean?.title?.title + "/" +
                        viewModel.episodesList[it].title
            )
        })

        srl_play_activity.isRefreshing = true
        viewModel.getPlayData(partUrl)
    }

    override fun onPause() {
        super.onPause()
        avp_play_activity.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        avp_play_activity.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoADManager.releaseAllVideos()
        orientationUtils?.releaseListener()
        avp_play_activity.release()
        avp_play_activity.setVideoAllCallBack(null)
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) return
        super.onBackPressed()
    }

    fun startPlay(url: String, title: String) {
        viewModel.mldAnimeEpisodeDataRefreshed.observe(this, {
            if (it) {
                avp_play_activity.startPlay()
            }
        })
        viewModel.refreshAnimeEpisodeData(url, title)
    }

    fun startPlay2(url: String, title: String) {
        avp_play_activity.startPlay(url, title)
    }

    private fun GSYVideoPlayer.startPlay(url: String = "", title: String = "") {
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        fullscreenButton.setOnClickListener {
            orientationUtils?.run { if (isLand != 1) resolveByClick() }
            avp_play_activity.startWindowFullscreen(this@PlayActivity, true, true)
        }
        //防止错位设置
        playTag = this.javaClass.simpleName
        //音频焦点冲突时是否释放
        isReleaseWhenLossAudio = false
        //增加封面
        val imageView = ImageView(this@PlayActivity)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //imageView.loadImage(it.cover?.detail ?: "")
        thumbImageView = imageView
        //thumbImageView.setOnClickListener { switchTitleBarVisible() }
        //是否开启自动旋转
        isRotateViewAuto = false
        //是否需要全屏锁定屏幕功能
        isNeedLockFull = true
        //是否可以滑动调整
        setIsTouchWiget(true)
        //设置触摸显示控制ui的消失时间
        dismissControlTime = 5000
        //设置播放过程中的回调
        setVideoAllCallBack(VideoCallPlayBack())
        //设置播放URL
        if (url == "") {
            setUp(
                viewModel.animeEpisodeDataBean.videoUrl,
                false, viewModel.animeEpisodeDataBean.title
            )
        } else {
            setUp(url, false, title)
        }
        //开始播放
        startPlayLogic()
    }

    private fun getSheetDialog(): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(this)
        val contentView = View.inflate(this, R.layout.dialog_bottom_sheet_1, null)
        bottomSheetDialog.setContentView(contentView)
        val flowLayout = contentView.findViewById<FlowLayout>(R.id.fl_dialog_bottom_sheet_1)
        viewModel.mldEpisodesList.observe(this, {
            flowLayout.removeAllViews()
            for (i in viewModel.episodesList.indices) {
                val tvFlowLayout: TextView = layoutInflater
                    .inflate(
                        R.layout.item_anime_episode_1,
                        flowLayout,
                        false
                    ) as TextView
                tvFlowLayout.text = viewModel.episodesList[i].title
                tvFlowLayout.setOnClickListener { it1 ->
                    "解析视频中...".showToast()
                    viewModel.getAnimeEpisodeData(viewModel.episodesList[i].actionUrl, i)
                }
                flowLayout.addView(tvFlowLayout)
            }
        })
        return bottomSheetDialog
    }

    inner class VideoCallPlayBack : GSYSampleCallBack() {
        override fun onPlayError(url: String?, vararg objects: Any?) {
            super.onPlayError(url, *objects)

            (objects[0].toString() + ", " + getString(R.string.get_data_failed)).showToast()
        }

        override fun onStartPrepared(url: String?, vararg objects: Any?) {
            super.onStartPrepared(url, *objects)
        }

        override fun onPrepared(url: String?, vararg objects: Any?) {
            super.onPrepared(url, *objects)

            //调整触摸滑动快进的比例
            Log.e("/*-", avp_play_activity.duration.toString())
            //毫秒,刚好划一屏1分35秒
            avp_play_activity.seekRatio = avp_play_activity.duration / 90_000f
        }

        override fun onClickBlank(url: String?, vararg objects: Any?) {
            super.onClickBlank(url, *objects)
//            switchTitleBarVisible()
        }

        override fun onClickStop(url: String?, vararg objects: Any?) {
            super.onClickStop(url, *objects)
//            delayHideBottomContainer()
        }

        override fun onAutoComplete(url: String?, vararg objects: Any?) {
            super.onAutoComplete(url, *objects)
        }
    }

    companion object {
        const val TAG = "PlayActivity"
    }
}