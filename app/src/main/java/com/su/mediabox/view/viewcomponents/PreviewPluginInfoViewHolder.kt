package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.bean.PreviewPluginInfo
import com.su.mediabox.databinding.ViewComponentPreviewPluginInfoBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder

class PreviewPluginInfoViewHolder private constructor(private val binding: ViewComponentPreviewPluginInfoBinding) :
    TypeViewHolder<PreviewPluginInfo>(binding.root) {

    private var tmpData: PreviewPluginInfo? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentPreviewPluginInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) {
        setOnClickListener(binding.vcPpAction) {
            tmpData?.also {
                PluginManager.downloadPlugin(it, true)
            }
        }
    }

    override fun onBind(data: PreviewPluginInfo) {
        tmpData = data
        binding.apply {
            data.apply {
                vcPpName.text = name
                vcPpVersion.text = version
                vcPpAction.text = "下载"
            }
        }
    }
}