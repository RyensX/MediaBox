package com.su.mediabox.view.viewcomponents.inner

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.databinding.ItemPluginBinding
import com.su.mediabox.databinding.ViewComponentMediaMoreBinding
import com.su.mediabox.plugin.PluginManager.launchPlugin
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder

class MediaMoreViewHolder private constructor(private val binding: ViewComponentMediaMoreBinding) :
    TypeViewHolder<MediaMoreViewHolder.DataStub>(binding.root) {

    companion object DataStub;

    constructor(parent: ViewGroup) : this(
        ViewComponentMediaMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.root.paint.apply {
            flags = Paint.UNDERLINE_TEXT_FLAG
            isAntiAlias = true
        }
    }

    override fun onBind(data: MediaMoreViewHolder.DataStub) {

    }
}