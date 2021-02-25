package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.util.dlna.CastObject
import com.skyd.imomoe.util.dlna.Utils
import com.skyd.imomoe.util.dlna.dmc.DLNACastManager
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface
import kotlinx.android.synthetic.main.activity_dlna_control.*
import kotlinx.android.synthetic.main.layout_circle_progress_text_tip_1.*
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.support.model.MediaInfo
import kotlin.collections.HashMap

class DlnaControlActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_dlna_control)

        setColorStatusBar(window, resources.getColor(R.color.gray_5))

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""
        deviceKey = intent.getStringExtra("deviceKey") ?: ""

        if (deviceKey.isEmpty()) {
            "数据接收错误！".showToast()
            finish()
        }

        //禁止发送数据时点击
        layout_dlna_control_activity_loading.isClickable = true
        layout_dlna_control_activity_loading.isFocusable = true
        tv_circle_progress_text_tip_1.text = getString(R.string.sending_data_please_wait)

        iv_dlna_control_activity_play.setOnClickListener {
            if (isPlaying) {
                pause()
            } else {
                play()
            }
        }

        iv_dlna_control_activity_stop.setOnClickListener { finish() }

        DLNACastManager.getInstance().registerActionCallbacks(
            object : ICastInterface.CastEventListener {
                override fun onSuccess(result: String) {
                    DLNACastManager.getInstance().getMediaInfo(deviceHashMap[deviceKey]
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
                    iv_dlna_control_activity_play.setImageResource(R.drawable.ic_pause_circle_white_24)

                    handler.postDelayed(positionRunnable, refreshPositionTime)

                    layout_dlna_control_activity_loading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("投屏失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.PauseEventListener {
                override fun onSuccess(result: Void?) {
                    "暂停播放".showToast()
                    isPlaying = false
                    iv_dlna_control_activity_play.setImageResource(R.drawable.ic_play_circle_white_24)

                    handler.post(positionRunnable)
                    handler.removeCallbacks(positionRunnable)

                    layout_dlna_control_activity_loading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("暂停失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.StopEventListener {
                override fun onSuccess(result: Void?) {
                    "停止投屏".showToast()
                    isPlaying = false
                    iv_dlna_control_activity_play.setImageResource(R.drawable.ic_play_circle_white_24)
//                    mPositionMsgHandler.stop()
//                    mVolumeMsgHandler.stop()
                    handler.post(positionRunnable)
                    handler.removeCallbacks(positionRunnable)

                    layout_dlna_control_activity_loading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("停止失败了\n$errMsg").showToast()
                }
            },
            object : ICastInterface.SeekToEventListener {
                override fun onSuccess(result: Long?) {
                    "快进到${Utils.getStringTime(result ?: 0)}".showToast()
                    play()
                    layout_dlna_control_activity_loading.gone()
                }

                override fun onFailed(errMsg: String?) {
                    ("快进失败了\n$errMsg").showToast()
                }
            }
        )

        sb_dlna_control_activity.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (durationMillSeconds > 0 && seekBar != null) {
                    val position =
                        (seekBar.progress * 1f / seekBar.max * durationMillSeconds).toInt()
                    layout_dlna_control_activity_loading.visible()
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

    private fun play() {
        layout_dlna_control_activity_loading.visible()
        DLNACastManager.getInstance().play()
    }

    private fun pause() {
        layout_dlna_control_activity_loading.visible()
        DLNACastManager.getInstance().pause()
    }

    private fun stop() {
        layout_dlna_control_activity_loading.visible()
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
                        if (layout_dlna_control_activity_loading.visibility != View.GONE)
                            layout_dlna_control_activity_loading.gone()
                        tv_dlna_control_activity_time.text = java.lang.String.format(
                            "%s / %s", positionInfo.relTime, positionInfo.trackDuration
                        )
                        if (positionInfo.trackDurationSeconds != 0L) {
                            durationMillSeconds = positionInfo.trackDurationSeconds * 1000
                            sb_dlna_control_activity.progress =
                                (positionInfo.trackElapsedSeconds * 100 / positionInfo.trackDurationSeconds).toInt()
                        } else {
                            sb_dlna_control_activity.progress = 0
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