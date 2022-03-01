package com.su.mediabox.view.viewcomponents

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.databinding.ItemPluginBinding
import com.su.mediabox.plugin.PluginManager.setPluginInfo
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.view.activity.MainActivity
import com.su.mediabox.view.adapter.type.TypeAdapter
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.typeAdapter

class ItemPluginViewHolder private constructor(private val binding: ItemPluginBinding) :
    TypeViewHolder<PluginInfo>(binding.root) {

    var adapter: TypeAdapter? = null

    constructor(parent: ViewGroup) : this(
        ItemPluginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        adapter = (parent as? RecyclerView)?.typeAdapter()
    }

    init {
        setOnClickListener(itemView) {
            itemView.context.goActivity<MainActivity>(Intent().setPluginInfo(it))
        }
        //TODO 暂时这样。后面做成展示插件详细信息
        setOnLongClickListener(itemView) { position ->
            adapter?.getData<PluginInfo>(position)?.also { data ->
                itemView.context.apply {
                    startActivity(Intent().also {
                        it.setClassName(data.packageName, data.pageActivity)
                    })
                }
            }
            true
        }
    }

    override fun onBind(data: PluginInfo) {
        binding.apply {
            pluginIcon.setImageDrawable(data.icon)
            pluginName.text = data.name
        }
    }
}