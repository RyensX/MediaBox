package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.databinding.ItemAnimeType1Binding
import com.su.mediabox.databinding.ViewComponentTagFlowBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.TagData
import com.su.mediabox.pluginapi.v2.been.TagFlowData
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.typeAdapter

class TagViewHolder private constructor(private val binding: ItemAnimeType1Binding) :
    TypeViewHolder<TagData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ItemAnimeType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) { pos ->
            bindingTypeAdapter?.getData<TagData>(pos)?.also {
                AppRouteProcessor.process(it.actionUrl)
            }
        }
    }

    override fun onBind(data: TagData) {
        binding.tvAnimeType1.text = data.name
    }
}