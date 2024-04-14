package com.su.mediabox.view.component.player.autoSkip

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.database.entity.SkipPosEntity
import com.su.mediabox.databinding.LayoutSkipDelToastBinding
import com.su.mediabox.databinding.LayoutVideoAutoSkipBinding
import com.su.mediabox.util.*
import com.su.mediabox.util.Text.ifBlank
import com.su.mediabox.view.component.player.VideoMediaPlayer
import com.su.mediabox.view.component.player.VideoMediaPlayerListener
import com.su.mediabox.view.component.player.VideoPositionMemoryDbStore
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


/**
 * 播放器-智能跳过
 *
 * Created by Ryens.
 * https://github.com/RyensX
 */
class VideoAutoSkipViewController private constructor(private val playerRef: WeakReference<VideoMediaPlayer>) :
    VideoMediaPlayerListener {
    constructor(player: VideoMediaPlayer) : this(WeakReference(player))

    private val tag = "VideoAutoViewController"

    private val activity: ComponentActivity?
        get() = playerRef.get()
            ?.let { if (it.context is ComponentActivity) it.context as ComponentActivity else null }

    private val player get() = playerRef.get()
    private val vm = activity?.viewModels<SkipPositionViewModel>()

    private val enterView = player?.findViewById<View>(R.id.iv_auto_skip_layout)
    private val layoutView = player?.findViewById<View>(R.id.layout_video_skip)
    private val binding = layoutView?.let { LayoutVideoAutoSkipBinding.bind(it) }

    private val mAdapter: SkipPosListAdapter

    private var currentTime = -1

    init {
        player?.addPlayerListener(this)
        layoutView?.gone()
        enterView?.setOnClickListener {
            layoutView?.visible()
        }
        mAdapter = SkipPosListAdapter(
            onEnable = { skip, enable ->
                vm?.value?.enable(skip.id, enable)
            },
            onClick = {
                skipVideo(vm?.value?.convertTime(currentTime) ?: -1, it)
            },
            onLongClick = {
                //TODO
            }
        )
        binding?.apply {
            vm?.value?.canSaveSkip?.observe(activity!!) {
                skipAdd.isEnabled = it
            }
            skipAdd.setOnClickListener {
                activity?.lifecycleScope?.launch {
                    val desc =
                        getTextDialog(activity!!, "添加智能跳过")?.ifBlank { "跳过项" } ?: return@launch
                    vm?.value?.putPlayPosition(desc = desc)
                    Log.d(tag, "putPlayPosition")
                }
            }
            skipPosList.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(skipPosList.context)
            }
            skipStart.setOnClickListener {
                vm?.value?.setStartTime(currentTime)
            }
            skipEnd.setOnClickListener {
                vm?.value?.setEndTime(currentTime)
            }
            vm?.value?.startTime?.observe(activity!!) {
                skipStart.text =
                    if (it == -1) ResourceUtil.getString(R.string.skip_start) else it.toString()
            }
            vm?.value?.endTime?.observe(activity!!) {
                skipEnd.text =
                    if (it == -1) ResourceUtil.getString(R.string.skip_end) else it.toString()
            }
        }
    }

    override fun onClickBlank() {
        layoutView?.gone()
    }

    override fun onUpdateVideoMedia(videoName: String, episodeName: String, url: String) {
        Log.d(tag, "onUpdateVideoMedia: videoName=$videoName episodeName=$episodeName url=$url")
        vm?.value?.resetTimeData()
        vm?.value?.getSkipPosList(videoName)?.observe(activity!!) {
            mAdapter.data = it
        }
    }

    override fun onPlayProgressUpdate(progress: Int) {
        currentTime = progress
        val sec = vm?.value?.convertTime(progress) ?: -1
        Log.d(tag, "onPlayProgressUpdate: ms=$progress sec=$sec")
        autoSkip(sec)
    }

    private fun autoSkip(sec: Int) {
        vm?.value?.checkSkip(sec) {
            Log.d(tag, "autoSkip: cur=$sec skip=$it")
            skipVideo(sec, it)
        }
    }

    private fun skipVideo(sec: Int, skipPosEntity: SkipPosEntity) {
        val target = sec * 1000 + skipPosEntity.duration
        player?.apply {
            seekTo(target)
            "智能跳转到 ${skipPosEntity.desc} (${VideoPositionMemoryDbStore.positionFormat(target)})".showToast()
        }
    }
}