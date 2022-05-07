package com.su.mediabox.view.viewcomponents

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.databinding.ViewComponentMeidaInfo1Binding
import com.su.mediabox.pluginapi.data.MediaInfo1Data
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.gone
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.visible
import com.su.mediabox.view.activity.MediaDetailActivity
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 媒体信息样式1视图组件
 */
class MediaInfo1ViewHolder private constructor(private val binding: ViewComponentMeidaInfo1Binding) :
    TypeViewHolder<MediaInfo1Data>(binding.root) {

    private val nameColor = binding.root.context.resources.getColor(R.color.foreground_black_skin)

    constructor(parent: ViewGroup) : this(
        ViewComponentMeidaInfo1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) {
        setOnClickListener(binding.root) { pos ->
            bindingTypeAdapter.getData<MediaInfo1Data>(pos)?.also {
                it.action?.go(itemView.context)
            }
        }
    }

    override fun onBind(dataMedia: MediaInfo1Data) {
        super.onBind(dataMedia)
        binding.vcGirdMediaItemTitle.apply {
            //自适应颜色，只有在详情页才需要是白色，其余的根据当前主题自动选择
            if (itemView.context.javaClass != MediaDetailActivity::class.java)
                setTextColor(nameColor)
            else
                setTextColor(Color.WHITE)
            text = dataMedia.name
        }
        //TODO 滚动延迟加载
        binding.vcGirdMediaItemCover.loadImage(dataMedia.coverUrl)
        binding.vcGirdMediaItemEpisode.apply {
            val other = dataMedia.other
            if (other.isBlank())
                gone()
            else {
                visible()
                setTextColor(getResColor(R.color.main_color_skin))
                text = dataMedia.other
            }
        }
    }

}