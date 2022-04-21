package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.databinding.ViewComponentVideoCover1Binding
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text
import com.su.mediabox.pluginapi.v2.been.VideoCover1Data
import com.su.mediabox.util.*
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 封面样式一视图组件
 */
class VideoCover1ViewHolder private constructor(private val binding: ViewComponentVideoCover1Binding) :
    TypeViewHolder<VideoCover1Data>(binding.root), Observer<HistoryBean?> {

    private var videoCover1Data: VideoCover1Data? = null
    private val isShowHistory = Pref.videoCover1ShowHistory

    constructor(parent: ViewGroup) : this(
        ViewComponentVideoCover1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            videoCover1Data?.action?.go(itemView.context)
        }
    }

    override fun onBind(data: VideoCover1Data) {
        super.onBind(data)
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
    }

    override fun onChanged(hb: HistoryBean?) {
        binding.vcVideoCover1ScoreHistory.apply {
            if (hb != null) {
                visible()
                text = String.format(
                    context.getString(R.string.cover_play_history_str_format),
                    hb.lastEpisode
                )
                tag = Text.buildRouteActionUrl(
                    Constant.ActionUrl.ANIME_PLAY,
                    hb.lastEpisodeUrl!!
                )
            } else
            //小心复用，所以主要主动隐藏
                gone()
        }
    }
}