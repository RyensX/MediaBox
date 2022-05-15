package com.su.mediabox.view.viewcomponents

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.databinding.ViewComponentMeidaInfo1Binding
import com.su.mediabox.pluginapi.data.MediaInfo1Data
import com.su.mediabox.util.*
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.view.activity.MediaDetailActivity
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 媒体信息样式1视图组件
 */
class MediaInfo1ViewHolder private constructor(private val binding: ViewComponentMeidaInfo1Binding) :
    TypeViewHolder<MediaInfo1Data>(binding.root) {

    protected val styleColor = getResColor(R.color.main_color_2_skin)

    constructor(parent: ViewGroup) : this(
        ViewComponentMeidaInfo1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) {
        setOnClickListener(binding.root) { pos ->
            bindingTypeAdapter.getData<MediaInfo1Data>(pos)?.also {
                it.action?.go(bindingContext)
            }
        }
    }

    override fun onBind(data: MediaInfo1Data) {
        super.onBind(data)
        binding.vcGirdMediaItemTitle.displayOnlyIfHasData(data.name) {
            gravity = data.gravity
            val color = data.nameColor ?: styleColor
            setTextColor(color)
            if (text != data.name)
                text = data.name
        }
        //TODO 滚动延迟加载
        binding.vcGirdMediaItemCover.apply {
            scaleType = data.coverScaleType
            layoutParams.height = data.coverHeight
            loadImage(data.coverUrl)
        }
        binding.vcGirdMediaItemEpisode.displayOnlyIfHasData(data.other) {
            gravity = data.gravity
            val color = data.otherColor ?: styleColor
            setTextColor(color)
            if (text != data.other)
                text = data.other
        }
        binding.vcMediaInfo1Other2.displayOnlyIfHasData(data.other2) {
            text = data.other2
        }
    }

}