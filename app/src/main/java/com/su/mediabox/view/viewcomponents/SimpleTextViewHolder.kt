package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.databinding.ViewComponentSimpleTextBinding
import com.su.mediabox.pluginapi.v2.been.SimpleTextData
import com.su.mediabox.util.setOnClickListener

class SimpleTextViewHolder private constructor(private val binding: ViewComponentSimpleTextBinding) :
    TextViewHolder<SimpleTextData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentSimpleTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go(itemView.context)
        }
    }

    override fun onBind(data: SimpleTextData) {
        super.onBind(data)
        binding.root.apply {
            text = data.text
        }
    }
}