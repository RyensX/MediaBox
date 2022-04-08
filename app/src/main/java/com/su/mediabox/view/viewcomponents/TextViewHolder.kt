package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.databinding.ViewComponentTextBinding
import com.su.mediabox.pluginapi.v2.been.TextData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder

class TextViewHolder private constructor(private val binding: ViewComponentTextBinding) :
    TypeViewHolder<TextData>(binding.root) {

    private var textData: TextData? = null

    private val styleColor = binding.root.resources.getColor(R.color.main_color_2_skin)

    constructor(parent: ViewGroup) : this(
        ViewComponentTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.action?.go()
        }
    }

    override fun onBind(data: TextData) {
        textData = data
        binding.root.apply {
            setTypeface(typeface, data.fontStyle)
            setTextColor(data.fontColor ?: styleColor)
            textSize = data.fontSize
            gravity = data.gravity
            setPadding(data.paddingLeft, data.paddingTop, data.paddingRight, data.paddingBottom)
            text = data.text
        }
    }
}