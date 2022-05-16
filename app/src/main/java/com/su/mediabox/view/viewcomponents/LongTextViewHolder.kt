package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.App
import com.su.mediabox.databinding.ViewComponentLongTextBinding
import com.su.mediabox.pluginapi.data.LongTextData
import com.su.mediabox.util.setOnClickListener

/**
 * 长文本视图组件，支持收缩（超过4行自动收缩）和链接
 */
class LongTextViewHolder private constructor(private val binding: ViewComponentLongTextBinding) :
    TextViewHolder<LongTextData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentLongTextBinding.inflate(LayoutInflater.from(App.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go(bindingContext)
        }
    }

    override fun onBind(data: LongTextData) {
        super.onBind(data)
        binding.root.apply {
            setContent(data.text)
        }
    }
}