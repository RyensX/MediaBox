package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.App
import com.su.mediabox.databinding.ViewComponentSimpleTextBinding
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.util.setOnClickListener

class SimpleTextViewHolder private constructor(private val binding: ViewComponentSimpleTextBinding) :
    TextViewHolder<SimpleTextData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        //全局视图组件创建视图应该使用ApplicationContext
        ViewComponentSimpleTextBinding.inflate(LayoutInflater.from(App.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go(bindingContext)
        }
    }

    override fun onBind(data: SimpleTextData) {
        super.onBind(data)
        binding.root.apply {
            text = data.text
        }
    }
}