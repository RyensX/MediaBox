package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.su.mediabox.R
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.databinding.ViewComponentVideoCover1Binding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text
import com.su.mediabox.pluginapi.v2.been.VideoCover1Data
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.gone
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.visible
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.v2.viewmodel.VideoDetailViewModel
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 封面样式一视图组件
 */
class VideoCover1ViewHolder private constructor(private val binding: ViewComponentVideoCover1Binding) :
    TypeViewHolder<VideoCover1Data>(binding.root) {

    companion object {
        fun bindHistoryInfo(textView: TextView, videoUrl: String) {
            var actionUrl = ""
            textView.apply {
                val activity = context as ComponentActivity

                gone()
                getAppDataBase().historyDao()
                    .getHistoryLiveData(videoUrl)
                    .also {
                        setOnClickListener {
                            AppRouteProcessor.process(actionUrl)
                        }
                        visible()
                    }
                    .observe(activity) { hb ->
                        if (hb != null) {
                            text = String.format(
                                context.getString(R.string.cover_play_history_str_format),
                                hb.lastEpisode
                            )
                            actionUrl = Text.buildRouteActionUrl(
                                Constant.ActionUrl.ANIME_PLAY,
                                hb.lastEpisodeUrl!!
                            )
                        } else
                        //小心复用，所以主要主动隐藏
                            gone()
                    }
            }
        }
    }

    private var videoCover1Data: VideoCover1Data? = null

    //在详情页打开才自动绑定历史播放信息
    //每次都必须重新获取，否则在非详情页被复用会有问题
    private val vm: VideoDetailViewModel?
        get() =
            (binding.root.context as ComponentActivity).let {
                if (it is VideoDetailActivity) ViewModelProvider(it)[VideoDetailViewModel::class.java] else null
            }


    constructor(parent: ViewGroup) : this(
        ViewComponentVideoCover1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            videoCover1Data?.actionUrl?.also {
                AppRouteProcessor.process(it)
            }
        }
    }

    override fun onBind(data: VideoCover1Data) {
        videoCover1Data = data
        //封面
        binding.vcVideoCover1Cover.loadImage(data.coverUrl)
        //评分
        binding.vcVideoCover1Score.apply {
            if (data.score == -1F)
                gone()
            else {
                visible()
                text = data.score.toString()
            }
        }
        //查找是否有观看记录
        vm?.also {
            bindHistoryInfo(binding.vcVideoCover1ScoreHistory, it.partUrl)
        } ?: binding.vcVideoCover1ScoreHistory.gone()
    }
}