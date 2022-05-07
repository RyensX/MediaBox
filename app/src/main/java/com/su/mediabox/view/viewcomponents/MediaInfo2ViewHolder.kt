package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import com.su.mediabox.databinding.ViewComponentMediaInfo2Binding
import com.su.mediabox.pluginapi.data.TagData
import com.su.mediabox.pluginapi.data.MediaInfo2Data
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.displayOnlyIfHasData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*

/**
 * 媒体信息样式2视图组件
 */
class MediaInfo2ViewHolder private constructor(private val binding: ViewComponentMediaInfo2Binding) :
    TypeViewHolder<MediaInfo2Data>(binding.root) {

    private var dataMedia: MediaInfo2Data? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentMediaInfo2Binding.inflate(
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
            dataMedia?.action?.go(itemView.context)
        }
    }

    override fun onBind(dataMedia: MediaInfo2Data) {
        super.onBind(dataMedia)
        this.dataMedia = dataMedia
        binding.apply {
            vcVideoLinearItemCover.displayOnlyIfHasData(dataMedia.coverUrl) { loadImage(it) }
            vcVideoLinearItemName.displayOnlyIfHasData(dataMedia.name) { text = it }
            vcVideoLinearItemEpisode.displayOnlyIfHasData(dataMedia.episodeInfo) { text = it }
            vcVideoLinearItemTagList.displayOnlyIfHasData(dataMedia.tagList) {
                typeAdapter().submitList(it)
            }
            vcVideoLinearItemDesc.displayOnlyIfHasData(dataMedia.desc) { text = it }

            //vcVideoLinearItemCustomData.displayOnlyIfHasData(data.customData) {
            //    typeAdapter().submitList(it)
            //}
        }
    }

}