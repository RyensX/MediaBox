package com.su.mediabox.view.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
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
import com.su.mediabox.Pref
import com.su.mediabox.R
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
import java.lang.Long.max
import java.lang.Long.min

class VideoMediaPlayActivity : BasePluginActivity(),
    VideoMediaPlayer.PlayOperatingProxy {

    companion object {
        var playList: List<EpisodeData>? = null
        private const val DEFAULT_SEEK_LENGTH = 15000L
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
                //??????
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
                //??????
                currentDanmakuData.observe(this@VideoMediaPlayActivity) {
                    it?.also { mBinding.vmPlay.setDanmakuData(it) }
                }
            }

            viewModel.playVideoMedia(action.episodeUrl)
        } ?: run {
            "??????????????????".showToast()
            finish()
        }

    }

    override fun onBackPressed() {
        if (viewModel.currentVideoPlayMedia.value is DataState.Failed && mBinding.vmLoadingLayer.isVisible)
            when (mBinding.vmPlay.currentState) {
                //????????????????????????????????????????????????????????????????????????
                GSYVideoView.CURRENT_STATE_PLAYING, GSYVideoView.CURRENT_STATE_PAUSE -> mBinding.vmLoadingLayer.gone()
                else -> super.onBackPressed()
            }
        else
            super.onBackPressed()
    }

    private fun init() {
        mBinding.vmPlay.run {
            playOperatingProxy = this@VideoMediaPlayActivity
            //????????????
            playPositionMemoryStore = VideoPositionMemoryDbStore
            //????????????
            orientationUtils = OrientationUtils(this@VideoMediaPlayActivity, this).apply {
                isRotateWithSystem = false
                if (screenType != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    resolveByClick()
                setLockClickListener { _, lock ->
                    isRotateWithSystem = lock
                }
            }

            ivDownloadButton?.gone()
            fullscreenButton.gone()
            //????????????????????????
            isRotateViewAuto = false
            //????????????????????????????????????
            isIfCurrentIsFullscreen = true
            isNeedLockFull = true
            //????????????????????????ui???????????????
            dismissControlTime = 5000
            //????????????????????????
            setIsTouchWiget(true)
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    this@run.currentPlayer.seekRatio = this@run.currentPlayer.duration / 90_000f
                    //????????????
                    viewModel.initDanmakuData()
                }

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    //?????????????????????
                    playNextEpisode()
                }
            })

            val playManager: Class<IPlayerManager> =
                Util.withoutExceptionGet { action.playerManager as? Class<IPlayerManager> }
                    ?:
                    //????????????????????????
                    Class.forName(Pref.playDefaultCore.value) as Class<IPlayerManager>
            PlayerFactory.setPlayManager(playManager)

            //TODO ???????????????
            GSYVideoType.enableMediaCodec()
        }

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    /**
     * ??????/??????/P - ??????/??????
     * Left - ??????
     * Right - ??????
     * Shift+Left - ??????0.5
     * Shift+Right - ??????0.5
     * M - ??????
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        mBinding.vmPlay.apply {
            when (keyCode) {
                //??????/??????
                KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_ENTER,
                    //????????????????????????WSA???????????????????????????
                KeyEvent.KEYCODE_P,
                KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_STOP -> gsyVideoManager.apply {
                    if (isPlaying) {
                        onVideoPause()
                    } else {
                        onVideoResume()
                    }
                }
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD ->
                    //??????
                    if (event?.isShiftPressed == true) {
                        speed -= 0.5F
                    }
                    //??????
                    else seekTo(
                        max(
                            DEFAULT_SEEK_LENGTH,
                            currentPositionWhenPlaying - DEFAULT_SEEK_LENGTH
                        )
                    )

                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_MEDIA_STEP_FORWARD ->
                    //??????
                    if (event?.isShiftPressed == true) {
                        speed += 0.5F
                    }
                    //??????
                    else seekTo(
                        min(
                            duration.toLong(),
                            currentPositionWhenPlaying + DEFAULT_SEEK_LENGTH
                        )
                    )
                //??????
                KeyEvent.KEYCODE_M ->
                    GSYVideoManager.instance().apply {
                        isNeedMute = !isNeedMute
                    }
            }
        }
        return super.onKeyDown(keyCode, event)
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
        //??????????????????
        playList = null
    }
}
