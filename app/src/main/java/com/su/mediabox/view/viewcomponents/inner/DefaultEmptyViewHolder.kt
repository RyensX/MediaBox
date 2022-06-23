package com.su.mediabox.view.viewcomponents.inner

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.bean.DefaultEmpty
import com.su.mediabox.databinding.ItemDefaultEmptyBinding
import com.su.mediabox.view.adapter.type.TypeViewHolder

class DefaultEmptyViewHolder private constructor(private val binding: ItemDefaultEmptyBinding) :
    TypeViewHolder<DefaultEmpty>(binding.root) {

    constructor(parent: ViewGroup) : this(
        ItemDefaultEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBind(data: DefaultEmpty) {
        super.onBind(data)
        binding.apply {
            emptyIcon.apply {
                layoutParams.width = data.iconSize
                setImageResource(data.icon)
            }
            emptyMsg.setText(data.msg)
        }
    }
}