package com.su.mediabox.view.viewcomponents

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.App
import com.su.mediabox.databinding.ViewComponentTagBinding
import com.su.mediabox.pluginapi.data.TagData
import com.su.mediabox.util.setOnClickListener

class TagViewHolder private constructor(private val binding: ViewComponentTagBinding) :
    TextViewHolder<TagData>(binding.vcTagText, binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go(bindingContext)
        }
    }

    override fun onBind(data: TagData) {
        super.onBind(data)
        binding.vcTagText.apply {
            //标签颜色,null则使用主题色
            backgroundTintList = ColorStateList.valueOf(data.tagColor ?: styleColor)

            text = data.name
        }
    }
}