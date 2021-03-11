package com.skyd.imomoe.view.activity

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivitySimplePlayBinding
import com.skyd.imomoe.util.MD5.getMD5
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.setFullScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            avpSimplePlayActivity.startPlayLogic()
            orientationUtils.resolveByClick()
            avpSimplePlayActivity.startWindowFullscreen(
                this@SimplePlayActivity,
                actionBar = true,
                statusBar = true
            )

            GlobalScope.launch(Dispatchers.IO) {
                val title = getAppDataBase().animeDownloadDao()
                    .getAnimeDownloadTitleByMd5(getMD5(File(url.replaceFirst("file://", ""))) ?: "")
                    ?: this@SimplePlayActivity.title
                runOnUiThread {
                    avpSimplePlayActivity.titleTextView?.text = title
                    avpSimplePlayActivity.fullWindowPlayer?.titleTextView?.text = title
                }
            }
        }
    }

    override fun getBinding(): ActivitySimplePlayBinding =
        ActivitySimplePlayBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.avpSimplePlayActivity.run {
            //设置旋转
            orientationUtils = OrientationUtils(this@SimplePlayActivity, this)
            getDownloadButton()?.gone()
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
            setUp(url, false, title)
        }
    }

    override fun onPause() {
        super.onPause()
        orientationUtils.setIsPause(true)
        mBinding.avpSimplePlayActivity.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        orientationUtils.setIsPause(false)
        mBinding.avpSimplePlayActivity.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.avpSimplePlayActivity.currentPlayer.release()
        mBinding.avpSimplePlayActivity.setVideoAllCallBack(null)
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(object : ContextWrapper(newBase) {
            override fun getSystemService(name: String): Any {
                // 解决AudioManager的内存泄漏
                return if (Context.AUDIO_SERVICE == name) {
                    applicationContext.getSystemService(name)
                } else super.getSystemService(name)
            }
        })
    }
}
