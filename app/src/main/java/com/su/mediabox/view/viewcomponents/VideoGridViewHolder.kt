package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.databinding.ViewComponentVideoGridBinding
import com.su.mediabox.pluginapi.v2.been.VideoGridData
import com.su.mediabox.view.adapter.decoration.AnimeCoverItemDecoration
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.grid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.typeAdapter

class VideoGridViewHolder private constructor(private val binding: ViewComponentVideoGridBinding) :
    TypeViewHolder<VideoGridData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentVideoGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.root
            .grid(4)
            .apply {
                addItemDecoration(AnimeCoverItemDecoration())
            }
            .initTypeList { }
    }

    override fun onBind(data: VideoGridData) {
        binding.root.typeAdapter().submitList(data.videos)
    }

}