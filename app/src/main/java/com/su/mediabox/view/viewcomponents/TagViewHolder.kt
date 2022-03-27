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
import com.su.mediabox.pluginapi.v2.been.TextData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.typeAdapter

class TagViewHolder private constructor(private val binding: ItemAnimeType1Binding) :
    TypeViewHolder<TagData>(binding.root) {

    private var data: TagData? = null

    constructor(parent: ViewGroup) : this(
        ItemAnimeType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) { pos ->
            data?.action?.go()
        }
    }

    override fun onBind(data: TagData) {
        this.data = data
        binding.tvAnimeType1.text = data.name
    }
}