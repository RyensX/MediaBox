package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.skyd.imomoe.R
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.MD5.getMD5
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.setFullScreen
import com.skyd.imomoe.util.Util.visible
import kotlinx.android.synthetic.main.activity_simple_play.*
import java.io.File


class SimplePlayActivity : BaseActivity() {
    private var url = ""
    private var title = ""
    private lateinit var orientationUtils: OrientationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_play)

        setFullScreen(window)

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""

        init()

        avp_simple_play_activity.startPlayLogic()
        orientationUtils.resolveByClick()

        Thread {
            val title = getAppDataBase().animeDownloadDao()
                .getAnimeDownload(getMD5(File(url.replaceFirst("file://", ""))) ?: "")?.title
            runOnUiThread {
                avp_simple_play_activity.titleTextView.text = title
            }
        }.start()
    }

    private fun init() {
        avp_simple_play_activity.run {
            //设置旋转
            orientationUtils = OrientationUtils(this@SimplePlayActivity, avp_simple_play_activity)
            getDownloadButton()?.gone()
            fullscreenButton.gone()

            //是否开启自动旋转
            isRotateViewAuto = false
            //是否需要全屏锁定屏幕功能
            isIfCurrentIsFullscreen = true
            isNeedLockFull = true
            //设置触摸显示控制ui的消失时间
            dismissControlTime = 5000
            //设置返回键
            backButton.visible()
            backButton.setOnClickListener { finish() }

            //是否可以滑动调整
            setIsTouchWiget(true)
            setUp(url, false, title)
        }
    }

    override fun onPause() {
        super.onPause()
        avp_simple_play_activity.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        avp_simple_play_activity.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
        avp_simple_play_activity.release()
        avp_simple_play_activity.setVideoAllCallBack(null)
    }
}