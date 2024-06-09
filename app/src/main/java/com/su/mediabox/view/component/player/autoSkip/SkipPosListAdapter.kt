package com.su.mediabox.view.component.player.autoSkip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.database.entity.SkipPosEntity
import com.su.mediabox.databinding.ItemSkipPosBinding
import com.su.mediabox.util.layoutInflater
import com.su.mediabox.view.component.player.VideoPositionMemoryDbStore


/**
 *
 * Created by Ryens.
 * https://github.com/RyensX
 */
class SkipPosListAdapter(
    private val onEnable: (SkipPosEntity, Boolean) -> Unit,
    private val onClick: (SkipPosEntity) -> Unit,
    private val onLongClick: (SkipPosEntity) -> Unit
) :
    RecyclerView.Adapter<SkipPosListAdapter.VH>() {

    class VH(val binding: ItemSkipPosBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(ItemSkipPosBinding.inflate(LayoutInflater.from(parent.context)))
    }

    var data: List<SkipPosEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent).apply {
        binding.itemSkipPosEnable.setOnClickListener { v ->
            data?.get(bindingAdapterPosition)?.let { onEnable(it, (v as CheckBox).isChecked) }
        }
        itemView.setOnClickListener {
            data?.get(bindingAdapterPosition)?.let { onClick(it) }
        }
        itemView.setOnLongClickListener {
            data?.get(bindingAdapterPosition)?.let { onLongClick(it) }
            true
        }
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {
        data?.get(position)?.let {
            holder.binding.itemSkipPosDesc.text = it.desc
            holder.binding.itemSkipPosEnable.isChecked = it.enable
            holder.binding.itemSkipPosTime.text =
                "${positionFormat(it.position)} + ${positionFormat(it.duration)}"
        }
    }

    private fun positionFormat(ms: Long) = VideoPositionMemoryDbStore.positionFormat(ms)

}