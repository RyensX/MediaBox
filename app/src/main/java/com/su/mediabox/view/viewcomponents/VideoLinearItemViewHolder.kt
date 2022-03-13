package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import com.su.mediabox.databinding.ViewComponentVideoLinearItemBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.TagData
import com.su.mediabox.pluginapi.v2.been.VideoLinearItemData
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.displayOnlyIfHasData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*

class VideoLinearItemViewHolder private constructor(private val binding: ViewComponentVideoLinearItemBinding) :
    TypeViewHolder<VideoLinearItemData>(binding.root) {

    private var data: VideoLinearItemData? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentVideoLinearItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    ) {
        binding.vcVideoLinearItemTagList
            .apply {
                layoutManager = FlexboxLayoutManager(binding.root.context)
            }
            .initTypeList(dataViewMap = DataViewMapList().registerDataViewMap<TagData, TagViewHolder>()) {}

        binding.vcVideoLinearItemCustomData
            .linear()
            .initTypeList { }

        setOnClickListener(binding.root) { pos ->
            data?.also {
                AppRouteProcessor.process(it.actionUrl)
            }
        }
    }

    override fun onBind(data: VideoLinearItemData) {
        this.data = data
        binding.apply {
            vcVideoLinearItemCover.displayOnlyIfHasData(data.coverUrl) { loadImage(it) }
            vcVideoLinearItemName.displayOnlyIfHasData(data.name) { text = it }
            vcVideoLinearItemEpisode.displayOnlyIfHasData(data.episodeInfo) { text = it }
            vcVideoLinearItemTagList.displayOnlyIfHasData(data.tagList) {
                typeAdapter().submitList(it)
            }
            vcVideoLinearItemDesc.displayOnlyIfHasData(data.desc) { text = it }

            //vcVideoLinearItemCustomData.displayOnlyIfHasData(data.customData) {
            //    typeAdapter().submitList(it)
            //}
        }
    }

}