package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.config.Api
import com.su.mediabox.databinding.ViewComponentVideoCover1Binding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.VideoCover1Data
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.gone
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.showToast
import com.su.mediabox.util.visible
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.skin.SkinManager

class VideoCover1ViewHolder private constructor(private val binding: ViewComponentVideoCover1Binding) :
    TypeViewHolder<VideoCover1Data>(binding.root) {

    private var videoCover1Data: VideoCover1Data? = null

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
        binding.vcVideoCover1Cover.loadImage(data.coverUrl)
        binding.vcVideoCover1Score.apply {
            if (data.score == -1F)
                gone()
            else {
                visible()
                text = data.score.toString()
            }
        }
    }
}