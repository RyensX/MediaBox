package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.databinding.ViewComponentLongTextBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.LongTextData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 长文本视图组件，支持收缩（超过4行自动收缩）和链接
 */
class LongTextViewHolder private constructor(private val binding: ViewComponentLongTextBinding) :
    TextViewHolder<LongTextData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentLongTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go()
        }
    }

    override fun onBind(data: LongTextData) {
        super.onBind(data)
        binding.root.apply {
            setContent(data.text)
        }
    }
}