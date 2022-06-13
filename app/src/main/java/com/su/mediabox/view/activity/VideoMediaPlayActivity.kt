package com.su.mediabox.view.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.IPlayerManager
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.su.mediabox.databinding.ActivityVideoMediaPlayBinding
import com.su.mediabox.pluginapi.action.PlayAction
import com.su.mediabox.pluginapi.data.EpisodeData
import com.su.mediabox.util.*
import com.su.mediabox.util.Util.setFullScreen
import com.su.mediabox.viewmodel.VideoMediaPlayViewModel
import com.su.mediabox.view.component.player.VideoMediaPlayer
import com.su.mediabox.view.component.player.VideoPositionMemoryDbStore
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoMediaPlayActivity : BasePluginActivity(),
    VideoMediaPlayer.PlayOperatingProxy {

    companion object {
        var playList: List<EpisodeData>? = null
    }

    private val mBinding by viewBind(ActivityVideoMediaPlayBinding::inflate)

    private lateinit var orientationUtils: OrientationUtils
    private val viewModel by viewModels<VideoMediaPlayViewModel>()

    private lateinit var action: PlayAction

    override val currentPlayEpisodeUrl: String get() = viewModel.currentPlayEpisodeUrl
    override fun playVideoMedia(episodeUrl: String) = viewModel.playVideoMedia(episodeUrl)
    override suspend fun putDanmaku(danmaku: String) = viewModel.putDanmaku(danmaku)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(window)

        getAction<PlayAction>()?.also { action ->
            this.action = action
            init()
            viewModel.apply {
                detailPartUrl = action.detailPartUrl
                coverUrl = action.coverUrl
                videoName = action.videoName
            }

            mBinding.vmErrorRetry.setOnClickListener { viewModel.playVideoMedia() }

            viewModel.apply {
                //视频
                currentVideoPlayMedia.observe(this@VideoMediaPlayActivity) { dataState ->
                    when (dataState) {
                        is DataState.Loading ->
                            mBinding.vmLoadingLayer.apply {
                                forEach {
                                    it.isVisible = it != mBinding.vmErrorRetry
                                }
                                visible()
                            }
                        is DataState.Success -> dataState.data?.also {
                            mBinding.vmPlay.playVideo(
                                it.videoPlayUrl,
                                it.title, viewModel.videoName
                            )
                            mBinding.vmLoadingLayer.gone()
                            mBinding.vmErrorRetry.gone()
                        }
                        is DataState.Failed -> {
                            dataState.throwable?.message?.showToast()
                            mBinding.vmLoadingLayer.apply {
                                forEach {
                                    it.isVisible = it == mBinding.vmErrorRetry
                                }
                                visible()
                            }
                        }
                        else -> Unit
                    }
                }
                //弹幕
                currentDanmakuData.observe(this@VideoMediaPlayActivity) {
                    it?.also { mBinding.vmPlay.setDanmakuData(it) }
                }
            }

            viewModel.playVideoMedia(action.episodeUrl)
        } ?: run {
            "播放信息错误".showToast()
            finish()
        }

    }

    override fun onBackPressed() {
        if (viewModel.currentVideoPlayMedia.value is DataState.Failed && mBinding.vmLoadingLayer.isVisible)
            when (mBinding.vmPlay.currentState) {
                //在已有正常播放时解析失败返回则只是关闭解析提示层
                GSYVideoView.CURRENT_STATE_PLAYING, GSYVideoView.CURRENT_STATE_PAUSE -> mBinding.vmLoadingLayer.gone()
                else -> super.onBackPressed()
            }
        else
            super.onBackPressed()
    }

    private fun init() {
        mBinding.vmPlay.run {
            playOperatingProxy = this@VideoMediaPlayActivity
            //进度记忆
            playPositionMemoryStore = VideoPositionMemoryDbStore
            //设置旋转
            orientationUtils = OrientationUtils(this@VideoMediaPlayActivity, this).apply {
                isRotateWithSystem = false
                if (screenType != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    resolveByClick()
            }
            ivDownloadButton?.gone()
            fullscreenButton.gone()
            //是否开启自动旋转
            isRotateViewAuto = false
            //是否需要全屏锁定屏幕功能
            isIfCurrentIsFullscreen = true
            isNeedLockFull = true
            //设置触摸显示控制ui的消失时间
            dismissControlTime = 5000
            //是否可以滑动调整
            setIsTouchWiget(true)
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    this@run.currentPlayer.seekRatio = this@run.currentPlayer.duration / 90_000f
                    //设置弹幕
                    viewModel.initDanmakuData()
                }

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    //自动切换下一集
                    playNextEpisode()
                }
            })

            val playManager =
                Util.withoutExceptionGet { action.playerManager as? Class<IPlayerManager> }
                    ?:
                    //自定义默认解码器
                    Exo2PlayerManager::class.java
            PlayerFactory.setPlayManager(playManager)

            //TODO 硬解码开关
            GSYVideoType.enableMediaCodec()
        }

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    override fun onPause() {
        super.onPause()
        orientationUtils.setIsPause(true)
        mBinding.vmPlay.currentPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        orientationUtils.setIsPause(false)
        mBinding.vmPlay.currentPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.vmPlay.currentPlayer.release()
        mBinding.vmPlay.setVideoAllCallBack(null)
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
        //释放播放列表
        playList = null
    }
}
