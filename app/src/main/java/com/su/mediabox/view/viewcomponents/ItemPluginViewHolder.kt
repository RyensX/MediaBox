package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.databinding.ItemPluginBinding
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder

class ItemPluginViewHolder private constructor(private val binding: ItemPluginBinding) :
    TypeViewHolder<PluginInfo>(binding.root) {

    private var pluginData: PluginInfo? = null

    constructor(parent: ViewGroup) : this(
        ItemPluginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    init {
        setOnClickListener(itemView) {
            itemView.context.launchPlugin(pluginData)
        }
    }

    override fun onBind(data: PluginInfo) {
        pluginData = data
        binding.apply {
            pluginIcon.setImageDrawable(data.icon)
            pluginName.text = data.name
        }
    }
}