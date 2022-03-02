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
    TypeViewHolder<LongTextData>(binding.root) {

    private var textData: LongTextData? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentLongTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            textData?.actionUrl?.also {
                AppRouteProcessor.process(it)
            }
        }
    }

    override fun onBind(data: LongTextData) {
        textData = data
        binding.root.apply {

            setTypeface(typeface, data.fontStyle)
            setTextColor(data.fontColor)
            textSize = data.fontSize
            gravity = data.gravity
            setPadding(paddingLeft, data.paddingTop, paddingRight, data.paddingBottom)
            setContent(data.text)
        }
    }
}