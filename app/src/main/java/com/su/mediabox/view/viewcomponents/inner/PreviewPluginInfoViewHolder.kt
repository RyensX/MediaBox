package com.su.mediabox.view.viewcomponents.inner

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.model.PreviewPluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ViewComponentPreviewPluginInfoBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.util.coil.CoilUtil.loadImage
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
                when (it.state) {
                    Const.Plugin.PLUGIN_STATE_UPDATABLE, Const.Plugin.PLUGIN_STATE_DOWNLOADABLE -> {
                        it.state = Const.Plugin.PLUGIN_STATE_DOWNLOADING
                        onBind(it)
                        PluginManager.downloadPlugin(it, true)
                    }
                    Const.Plugin.PLUGIN_STATE_OPEN -> bindingContext.apply {
                        launchPlugin(it)
                    }
                }
            }
        }
    }

    override fun onBind(data: PreviewPluginInfo) {
        tmpData = data
        binding.apply {
            data.apply {
                vcPpName.text = name
                vcPpVersion.text = version
                vcPpIcon.loadImage(iconBase64)

                vcPpAction.text = when (state) {
                    Const.Plugin.PLUGIN_STATE_DOWNLOADING -> "下载中"
                    Const.Plugin.PLUGIN_STATE_UPDATABLE -> "更新"
                    Const.Plugin.PLUGIN_STATE_OPEN -> "打开"
                    else -> "下载"
                }
            }
        }
    }
}