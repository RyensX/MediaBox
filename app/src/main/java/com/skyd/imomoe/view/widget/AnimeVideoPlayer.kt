package com.skyd.imomoe.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R

class AnimeVideoPlayer : StandardGSYVideoPlayer {

    private var initFirstLoad = true

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = R.layout.layout_anime_video_player

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_pause_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_pause_main_color_bg)
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_play_main_color_bg)
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageResource(R.drawable.ic_refresh_white_24)
                    imageView.setBackgroundResource(0)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_play_main_color_bg)
                }
            }

        } else {
            super.updateStartImage()
        }
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        initFirstLoad = true
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        mBottomContainer.visibility = View.GONE
        mStartButton.visibility = View.GONE
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        if (initFirstLoad) {
            mBottomContainer.visibility = View.GONE
            mStartButton.visibility = View.GONE
        }
        initFirstLoad = false
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        mBottomContainer.visibility = View.GONE
    }

    fun getBottomContainer() = mBottomContainer
}