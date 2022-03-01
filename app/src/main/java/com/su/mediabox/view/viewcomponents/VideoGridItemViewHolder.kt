package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.databinding.ItemAnimeCover1Binding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.VideoGridItemData
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.gone
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.visible
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.typeAdapter

class VideoGridItemViewHolder private constructor(private val binding: ItemAnimeCover1Binding) :
    TypeViewHolder<VideoGridItemData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ItemAnimeCover1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        val adapter = (parent as? RecyclerView)?.typeAdapter()

        setOnClickListener(binding.root) { pos ->
            adapter?.getData<VideoGridItemData>(pos)?.also {
                AppRouteProcessor.process(it.actionUrl)
            }
        }
    }

    override fun onBind(data: VideoGridItemData) {

        binding.tvAnimeCover1Title.apply {
            setTextColor(getResColor(R.color.foreground_white_skin))
            text = data.name
        }
        binding.ivAnimeCover1Cover.loadImage(data.coverUrl)
        binding.tvAnimeCover1Episode.apply {
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