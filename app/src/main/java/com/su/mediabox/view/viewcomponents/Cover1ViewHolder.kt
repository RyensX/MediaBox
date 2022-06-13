package com.su.mediabox.view.viewcomponents

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.App
import com.su.mediabox.databinding.ViewComponentCover1Binding
import com.su.mediabox.pluginapi.data.Cover1Data
import com.su.mediabox.util.*
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.view.adapter.type.TypeViewHolder

/**
 * 封面样式一视图组件
 */
class Cover1ViewHolder private constructor(private val binding: ViewComponentCover1Binding) :
    TypeViewHolder<Cover1Data>(binding.root) {

    private var cover1Data: Cover1Data? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentCover1Binding.inflate(LayoutInflater.from(App.context), parent, false)
    ) {
        setOnClickListener(binding.root) {
            cover1Data?.action?.go(bindingContext)
        }
    }

    override fun onBind(data: Cover1Data) {
        super.onBind(data)
        cover1Data = data
        //封面
        binding.vcCover1Cover.loadImage(data.coverUrl)
        //评分
        binding.vcCover1Score.apply {
            if (data.score == -1F)
                gone()
            else {
                visible()
                text = data.score.toString()
            }
        }
    }

}