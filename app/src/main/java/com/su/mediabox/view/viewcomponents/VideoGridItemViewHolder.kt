package com.su.mediabox.view.viewcomponents

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.databinding.ViewComponentGridMeidaItemBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.VideoGridItemData
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.gone
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.visible
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.view.adapter.type.TypeViewHolder

class VideoGridItemViewHolder private constructor(private val binding: ViewComponentGridMeidaItemBinding) :
    TypeViewHolder<VideoGridItemData>(binding.root) {

    private val nameColor = binding.root.context.resources.getColor(R.color.foreground_black_skin)

    constructor(parent: ViewGroup) : this(
        ViewComponentGridMeidaItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) {
        setOnClickListener(binding.root) { pos ->
            bindingTypeAdapter.getData<VideoGridItemData>(pos)?.also {
                it.action?.go(itemView.context)
            }
        }
    }

    override fun onBind(data: VideoGridItemData) {
        super.onBind(data)
        binding.vcGirdMediaItemTitle.apply {
            setTextColor(getResColor(R.color.foreground_white_skin))
            text = data.name
            //自适应颜色，只有在详情页才需要是白色，其余的根据当前主题自动选择
            if (AppRouteProcessor.currentActivity?.get()?.javaClass != VideoDetailActivity::class.java)
                setTextColor(nameColor)
            else
                setTextColor(Color.WHITE)
        }
        //TODO 滚动延迟加载
        binding.vcGirdMediaItemCover.loadImage(data.coverUrl)
        binding.vcGirdMediaItemEpisode.apply {
            val other = data.other
            if (other.isBlank())
                gone()
            else {
                visible()
                setTextColor(getResColor(R.color.main_color_skin))
                text = data.other
            }
        }
    }

}