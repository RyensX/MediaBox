package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivitySimplePlayBinding
import com.skyd.imomoe.util.Util.setFullScreen
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.toMD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File


class SimplePlayActivity : BaseActivity<ActivitySimplePlayBinding>() {
    private var url = ""
    private var title = ""
    private lateinit var orientationUtils: OrientationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(window)

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""

        init()

        mBinding.run {
            PlayerFactory.setPlayManager(Exo2PlayerManager().javaClass)
            GSYVideoType.disableMediaCodec()        // 关闭硬解码
            avpSimplePlayActivity.startPlayLogic()
            orientationUtils.resolveByClick()
            avpSimplePlayActivity.startWindowFullscreen(
                this@SimplePlayActivity,
                actionBar = true,
                statusBar = true
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val title = getAppDataBase().animeDownloadDao()
                    .getAnimeDownloadTitleByMd5(File(url.replaceFirst("file://", "")).toMD5() ?: "")
                    ?: this@SimplePlayActivity.title
                runOnUiThread {
                    avpSimplePlayActivity.titleTextView?.text = title
                    avpSimplePlayActivity.fullWindowPlayer?.titleTextView?.text = title
                }
            }
        }

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    override fun getBinding(): ActivitySimplePlayBinding =
        ActivitySimplePlayBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.avpSimplePlayActivity.run {
            //设置旋转
            orientationUtils = OrientationUtils(this@SimplePlayActivity, this)
            getDownloadButton()?.gone()
            setEpisodeButtonVisibility(View.GONE)
            fullscreenButton.gone()
            //是否开启自动旋转
            isRotateViewAuto = false
            //是否需要全屏锁定屏幕功能
            isIfCurrentIsFullscreen = true
            isNeedLockFull = true
            //设置触摸显示控制ui的消失时间
            dismissControlTime = 5000
            //设置退出全屏的监听器
            setBackFromFullScreenListener { finish() }
            //是否可以滑动调整
            setIsTouchWiget(true)
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    this@run.currentPlayer.seekRatio = this@run.currentPlayer.duration / 90_000f
                }
            })
            setUp(url, false, title)
        }
    }

    override fun onPause() {
        super.onPause()
        orientationUtils.setIsPause(true)
        mBinding.avpSimplePlayActivity.currentPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        orientationUtils.setIsPause(false)
        mBinding.avpSimplePlayActivity.currentPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.avpSimplePlayActivity.currentPlayer.release()
        mBinding.avpSimplePlayActivity.setVideoAllCallBack(null)
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
    }
}
