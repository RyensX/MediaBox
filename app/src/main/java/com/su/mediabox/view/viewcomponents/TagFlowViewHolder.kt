package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.su.mediabox.App
import com.su.mediabox.databinding.ViewComponentGridBinding
import com.su.mediabox.pluginapi.data.TagFlowData
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.typeAdapter


class TagFlowViewHolder private constructor(private val binding: ViewComponentGridBinding) :
    TypeViewHolder<TagFlowData>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ViewComponentGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.root.apply {
            layoutManager = FlexboxLayoutManager(bindingContext).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
        }.initTypeList { }
    }

    override fun onBind(data: TagFlowData) {
        super.onBind(data)
        binding.root.typeAdapter().submitList(data.tagList)
    }
}