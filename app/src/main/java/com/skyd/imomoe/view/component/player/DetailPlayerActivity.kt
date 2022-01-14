package com.skyd.imomoe.view.component.player

import android.content.res.Configuration
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import androidx.viewbinding.ViewBinding
import com.skyd.imomoe.view.activity.BaseActivity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import java.lang.NullPointerException

/**
 * 详情模式播放页面基础类
 */
abstract class DetailPlayerActivity<T : GSYBaseVideoPlayer, VB : ViewBinding> : BaseActivity<VB>(),
    MyVideoAllCallBack {
    protected open var isPlay = false

    // 是否是在onPause方法里自动暂停的
    protected open var isPause = false
    protected open var orientationUtils: AnimeOrientationUtils? = null

    /**
     * 选择普通模式
     */
    protected open fun initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = AnimeOrientationUtils(this, getGSYVideoPlayer(), orientationOption).apply {
            // 初始化不打开外部的旋转
            isEnable = false
        }
        if (getGSYVideoPlayer().fullscreenButton != null) {
            getGSYVideoPlayer().fullscreenButton.setOnClickListener {
                showFull()
                clickForFullScreen()
            }
        }
        // 退出全屏监听，避免平板退出全屏后变成竖屏
        getGSYVideoPlayer().setBackFromFullScreenListener { onBackPressed() }
    }

    /**
     * 选择builder模式
     */
    fun initVideoBuilderMode() {
        initVideo()
        gsyVideoOptionBuilder.setVideoAllCallBack(this).build(getGSYVideoPlayer())
    }

    protected open fun showFull() {
        if (orientationUtils?.isLand != 1) {
            //直接横屏
            orientationUtils?.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusBar
        getGSYVideoPlayer().startWindowFullscreen(
            this@DetailPlayerActivity,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull()
        )
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo2()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        if (getGSYVideoPlayer().currentPlayer.currentState != GSYVideoView.CURRENT_STATE_PAUSE) {
            getGSYVideoPlayer().currentPlayer.onVideoPause()
            orientationUtils?.setIsPause(true)
            isPause = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            getGSYVideoPlayer().currentPlayer.onVideoResume()
            orientationUtils?.setIsPause(false)
            isPause = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            getGSYVideoPlayer().currentPlayer.release()
        }
        orientationUtils?.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            getGSYVideoPlayer().onConfigurationChanged(
                this,
                newConfig,
                orientationUtils,
                hideActionBarWhenFull(),
                hideStatusBarWhenFull()
            )
        }
    }

    override fun onStartPrepared(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(true)
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        orientationUtils.let {
            if (it == null) {
                throw NullPointerException("initVideo() or initVideoBuilderMode() first")
            }
            // 开始播放了才能旋转和全屏
            it.isEnable = detailOrientationRotateAuto && !isAutoFullWithSize
            isPlay = true
            isPause = false
            videoPlayStatusChanged(true)
        }
    }

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {}
    override fun onClickStartError(url: String?, vararg objects: Any?) {}
    override fun onClickStop(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(false)
    }

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(false)
    }

    override fun onClickResume(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(true)
    }

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(true)
    }

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {}
    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {}
    override fun onAutoComplete(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(false)
    }

    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {}
    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        orientationUtils?.backToProtVideo()
    }

    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {}
    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {}
    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {}
    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {}
    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {}
    override fun onPlayError(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(false)
    }

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {}
    override fun onClickBlank(url: String?, vararg objects: Any?) {}
    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {}
    override fun onComplete(url: String?, vararg objects: Any?) {
        videoPlayStatusChanged(false)
    }

    protected open fun hideActionBarWhenFull(): Boolean = true

    protected open fun hideStatusBarWhenFull(): Boolean = true

    /**
     * 可配置旋转 OrientationUtils
     */
    protected open val orientationOption: OrientationOption?
        get() = null

    /**
     * 播放控件
     */
    abstract fun getGSYVideoPlayer(): T

    /**
     * 配置播放器
     */
    abstract val gsyVideoOptionBuilder: GSYVideoOptionBuilder

    /**
     * 点击了全屏
     */
    abstract fun clickForFullScreen()

    /**
     * 是否启动旋转横屏，true表示启动
     */
    abstract val detailOrientationRotateAuto: Boolean

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    protected open val isAutoFullWithSize: Boolean
        get() = false

    override fun onVideoPause() {
        videoPlayStatusChanged(false)
    }

    override fun onVideoResume() {
        videoPlayStatusChanged(true)
    }

    /**
     * 视频播放状态变化
     *
     * @param playing false：未在播放（包括播放失败暂停等等）；true：正在播放（包括正在准备加载、缓冲等等）
     */
    protected open fun videoPlayStatusChanged(playing: Boolean) {}
}