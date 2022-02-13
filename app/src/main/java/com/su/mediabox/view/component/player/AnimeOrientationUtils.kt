package com.su.mediabox.view.component.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import java.lang.ref.WeakReference

class AnimeOrientationUtils : OrientationUtils {
    private val LAND_TYPE_NULL = 0
    private val LAND_TYPE_NORMAL = 1
    private val LAND_TYPE_REVERSE = 2
    private val mVideoPlayer: GSYBaseVideoPlayer?
    private val mActivity: WeakReference<Activity>

    constructor(activity: Activity, gsyVideoPlayer: GSYBaseVideoPlayer?) :
            super(activity, gsyVideoPlayer) {
        mActivity = WeakReference(activity)
        mVideoPlayer = gsyVideoPlayer
    }

    constructor(
        activity: Activity,
        gsyVideoPlayer: GSYBaseVideoPlayer?,
        orientationOption: OrientationOption?
    ) : super(activity, gsyVideoPlayer, orientationOption) {
        mActivity = WeakReference(activity)
        mVideoPlayer = gsyVideoPlayer
    }

    private fun setRequestedOrientation2(requestedOrientation: Int) {
        val activity = mActivity.get() ?: return
        try {
            activity.requestedOrientation = requestedOrientation
        } catch (exception: IllegalStateException) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                Debuger.printfError("OrientationUtils", exception)
            } else {
                exception.printStackTrace()
            }
        }
    }

    fun backToProtVideo2(): Int {
        if (isLand > LAND_TYPE_NULL) {
            isClick = true
            setRequestedOrientation2(ActivityInfo.SCREEN_ORIENTATION_USER)
            if (mVideoPlayer != null && mVideoPlayer.fullscreenButton != null) mVideoPlayer.fullscreenButton.setImageResource(
                mVideoPlayer.enlargeImageRes
            )
            isLand = LAND_TYPE_NULL
            isClickPort = false
            return 500
        }
        return LAND_TYPE_NULL
//        return super.backToProtVideo()
    }
}