package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityDlnaControlBinding
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.dlna.CastObject
import com.skyd.imomoe.util.dlna.Utils
import com.skyd.imomoe.util.dlna.dmc.DLNACastManager
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible
import org.fourthline.cling.model.meta.Device
import kotlin.collections.HashMap

class DlnaControlActivity : BaseActivity<ActivityDlnaControlBinding>() {
    private lateinit var layoutDlnaControlActivityLoading: RelativeLayout
    private lateinit var deviceKey: String
    private lateinit var url: String
    private lateinit var title: String
    private var isPlaying = false

    companion object {
        const val TAG = "DlnaControlActivity"
        val deviceHashMap = HashMap<String, Device<*, *, *>?>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setColorStatusBar(window, getResColor(R.color.gray_5))

        layoutDlnaControlActivityLoading =
            mBinding.layoutDlnaControlActivityLoading.layoutCircleProgressTextTip1

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""
        deviceKey = intent.getStringExtra("deviceKey") ?: ""

        if (deviceKey.isEmpty()) {
            "数据接收错误！".showToast()
            finish()
        }

        //禁止发送数据时点击
        mBinding.run {
            layoutDlnaControlActivityLoading.layoutCircleProgressTextTip1.isClickable = true
            layoutDlnaControlActivityLoading.layoutCircleProgressTextTip1.isFocusable = true
            layoutDlnaControlActivityLoading.tvCircleProgressTextTip1.text =
                getString(R.string.sending_data_please_wait)

            ivDlnaControlActivityPlay.setOnClickListener {
                if (isPlaying) {
                    pause()
                } else {
                    play()
                }
            }

            ivDlnaControlActivityStop.setOnClickListener { finish() }
        }


        DLNACastManager.getInstance().registerActionCallbacks(
            object : ICastInterface.CastEventListener {
                override fun onSuccess(result: String) {
                    DLNACastManager.getInstance().getMediaInfo(
                        deviceHashMap[deviceKey]
                    ) { t, errMsg ->
                        Log.i(TAG, t?.currentURI.toString())
                    }
                    if (!isPlaying) play()
                }

                override fun onFailed(errMsg: String?) {
                    ("投屏失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.PlayEventListener {
                override fun onSuccess(result: Void?) {
                    "开始播放".showToast()
                    isPlaying = true
                    mBinding.ivDlnaControlActivityPlay.setImageResource(R.drawable.ic_pause_circle_white_24)

                    handler.postDelayed(positionRunnable, refreshPositionTime)

                    layoutDlnaControlActivityLoading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("投屏失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.PauseEventListener {
                override fun onSuccess(result: Void?) {
                    "暂停播放".showToast()
                    isPlaying = false
                    mBinding.ivDlnaControlActivityPlay.setImageResource(R.drawable.ic_play_circle_white_24)

                    handler.post(positionRunnable)
                    handler.removeCallbacks(positionRunnable)

                    layoutDlnaControlActivityLoading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("暂停失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.StopEventListener {
                override fun onSuccess(result: Void?) {
                    "停止投屏".showToast()
                    isPlaying = false
                    mBinding.ivDlnaControlActivityPlay.setImageResource(R.drawable.ic_play_circle_white_24)
//                    mPositionMsgHandler.stop()
//                    mVolumeMsgHandler.stop()
                    handler.post(positionRunnable)
                    handler.removeCallbacks(positionRunnable)

                    layoutDlnaControlActivityLoading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("停止失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.SeekToEventListener {
                override fun onSuccess(result: Long?) {
                    "快进到${Utils.getStringTime(result ?: 0)}".showToast()
                    play()
                    layoutDlnaControlActivityLoading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("快进失败了\n$errMsg").showToast()
                }
            }
        )

        mBinding.sbDlnaControlActivity.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (durationMillSeconds > 0 && seekBar != null) {
                    val position =
                        (seekBar.progress * 1f / seekBar.max * durationMillSeconds).toInt()
                    layoutDlnaControlActivityLoading.visible()
                    DLNACastManager.getInstance().seekTo(position.toLong())
                }
            }
        })

        deviceHashMap[deviceKey]?.let {
            DLNACastManager.getInstance().cast(
                it,
                CastObject.CastVideo.newInstance(
                    url,
                    System.currentTimeMillis().toString(),
                    title
                )
            )
        }
    }

    override fun getBinding(): ActivityDlnaControlBinding =
        ActivityDlnaControlBinding.inflate(layoutInflater)

    private fun play() {
        layoutDlnaControlActivityLoading.visible()
        DLNACastManager.getInstance().play()
    }

    private fun pause() {
        layoutDlnaControlActivityLoading.visible()
        DLNACastManager.getInstance().pause()
    }

    private fun stop() {
        layoutDlnaControlActivityLoading.visible()
        DLNACastManager.getInstance().stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        DLNACastManager.getInstance().unregisterActionCallbacks()
        deviceHashMap.remove(deviceKey)
    }

    private var durationMillSeconds: Long = 0
    private val refreshPositionTime: Long = 500

    private val positionRunnable: Runnable = object : Runnable {
        override fun run() {
            if (deviceHashMap[deviceKey] == null) return
            DLNACastManager.getInstance()
                .getPositionInfo(deviceHashMap[deviceKey]) { positionInfo, errMsg ->
                    if (positionInfo != null) {
                        if (layoutDlnaControlActivityLoading.visibility != View.GONE)
                            layoutDlnaControlActivityLoading.gone()
                        mBinding.tvDlnaControlActivityTime.text = java.lang.String.format(
                            "%s / %s", positionInfo.relTime, positionInfo.trackDuration
                        )
                        if (positionInfo.trackDurationSeconds != 0L) {
                            durationMillSeconds = positionInfo.trackDurationSeconds * 1000
                            mBinding.sbDlnaControlActivity.progress =
                                (positionInfo.trackElapsedSeconds * 100 / positionInfo.trackDurationSeconds).toInt()
                        } else {
                            mBinding.sbDlnaControlActivity.progress = 0
                        }
                    } else {
                        Log.e(TAG, errMsg.toString())
                    }
                    handler.postDelayed(this, refreshPositionTime)
                }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
}
