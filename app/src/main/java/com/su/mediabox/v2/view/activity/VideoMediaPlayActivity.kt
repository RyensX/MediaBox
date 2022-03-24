package com.su.mediabox.v2.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.su.mediabox.databinding.ActivityVideoMediaPlayBinding
import com.su.mediabox.util.Util.setFullScreen
import com.su.mediabox.util.gone
import com.su.mediabox.v2.viewmodel.VideoMediaPlayViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.component.player.VideoPositionMemoryDbStore
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoMediaPlayActivity : BasePluginActivity<ActivityVideoMediaPlayBinding>() {

    private lateinit var orientationUtils: OrientationUtils
    private val viewModel by viewModels<VideoMediaPlayViewModel>()

    companion object {
        const val INTENT_EPISODE = "episodeUrl"
        const val INTENT_COVER = "coverUrl"
        const val INTENT_DPU = "detailPartUrl"
        const val INTENT_NAME = "videoName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(window)

        viewModel.apply {
            detailPartUrl = intent.getStringExtra(INTENT_DPU) ?: ""
            coverUrl = intent.getStringExtra(INTENT_COVER) ?: ""
            videoName = intent.getStringExtra(INTENT_NAME) ?: ""
        }

        init()

        viewModel.apply {
            //视频
            currentVideoPlayMedia.observe(this@VideoMediaPlayActivity) {
                mBinding.vmPlay.apply {
                    setUp(
                        it.videoPlayUrl, false,
                        String.format("%s %s", viewModel.videoName, it.title)
                    )
                    startPlayLogic()
                }
            }
            //弹幕
            currentDanmakuData.observe(this@VideoMediaPlayActivity) {
                mBinding.vmPlay.setDanmakuUrl(it.first, it.second)
            }
            //TODO 选集传递，支持快速切换和上下切换
        }

        viewModel.playVideoMedia(intent.getStringExtra(INTENT_EPISODE) ?: "")
    }

    override fun getBinding() = ActivityVideoMediaPlayBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.vmPlay.run {
            //进度记忆
            playPositionMemoryStore = VideoPositionMemoryDbStore
            //设置旋转
            orientationUtils = OrientationUtils(this@VideoMediaPlayActivity, this)
            orientationUtils.resolveByClick()
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
                    finish()
                    //TODO 自动跳转下一集
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
    }
}
