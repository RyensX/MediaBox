package com.su.mediabox.view.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.su.mediabox.databinding.ActivityVideoMediaPlayBinding
import com.su.mediabox.pluginapi.action.PlayAction
import com.su.mediabox.pluginapi.data.EpisodeData
import com.su.mediabox.util.Util.setFullScreen
import com.su.mediabox.util.getAction
import com.su.mediabox.util.gone
import com.su.mediabox.util.showToast
import com.su.mediabox.viewmodel.VideoMediaPlayViewModel
import com.su.mediabox.view.component.player.VideoMediaPlayer
import com.su.mediabox.view.component.player.VideoPositionMemoryDbStore
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoMediaPlayActivity : BasePluginActivity<ActivityVideoMediaPlayBinding>() {

    private lateinit var orientationUtils: OrientationUtils
    private val viewModel by viewModels<VideoMediaPlayViewModel>()

    companion object {
        @Deprecated("全部更新V2后移除")
        const val INTENT_EPISODE = "episodeUrl"

        @Deprecated("全部更新V2后移除")
        const val INTENT_COVER = "coverUrl"

        @Deprecated("全部更新V2后移除")
        const val INTENT_DPU = "detailPartUrl"

        @Deprecated("全部更新V2后移除")
        const val INTENT_NAME = "videoName"

        var playList: List<EpisodeData>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        VideoMediaPlayer.playViewModel = viewModel
        super.onCreate(savedInstanceState)

        setFullScreen(window)
        init()

        getAction<PlayAction>()?.also { action ->
            viewModel.apply {
                detailPartUrl = action.detailPartUrl
                coverUrl = action.coverUrl
                videoName = action.videoName
            }

            viewModel.apply {
                //视频
                currentVideoPlayMedia.observe(this@VideoMediaPlayActivity) {
                    mBinding.vmPlay.playVideo(it.videoPlayUrl, it.title, viewModel.videoName)
                }
                //弹幕
                currentDanmakuData.observe(this@VideoMediaPlayActivity) {
                    mBinding.vmPlay.setDanmakuUrl(it.first, it.second)
                }
            }

            viewModel.playVideoMedia(action.episodeUrl)
        } ?: run {
            "播放信息错误".showToast()
            finish()
        }

    }

    override fun getBinding() = ActivityVideoMediaPlayBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.vmPlay.run {
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

            PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
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
        VideoMediaPlayer.playViewModel = null
    }
}
