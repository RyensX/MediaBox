package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.databinding.ViewComponentTextBinding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.TextData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.typeAdapter

class TextViewHolder private constructor(private val binding: ViewComponentTextBinding) :
    TypeViewHolder<TextData>(binding.root) {

    private var textData: TextData? = null

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
            setTextColor(data.fontColor)
            textSize = data.fontSize
            gravity = data.gravity
            setPadding(paddingLeft, data.paddingTop, paddingRight, data.paddingBottom)
            text = data.text
        }
    }
}