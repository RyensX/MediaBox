package com.su.mediabox.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.model.PluginManageModel

object PluginManageDiff : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(
        oldItem: Any,
        newItem: Any
    ) = oldItem.javaClass == newItem.javaClass && when (oldItem) {
        is PluginManageModel -> (newItem as PluginManageModel).run {
            oldItem.pluginInfo.id == pluginInfo.id
        }
        is MediaFavorite -> (newItem as MediaFavorite).run {
            oldItem.mediaUrl == mediaUrl
        }
        else -> false
    }

    override fun areContentsTheSame(
        oldItem: Any,
        newItem: Any
    ) = when (oldItem) {
        is PluginManageModel -> (newItem as PluginManageModel).run {
            oldItem.isExpand == isExpand && oldItem.childData?.size == childData?.size
        }
        is MediaFavorite -> (newItem as MediaFavorite).run {
            oldItem.cover == cover && oldItem.mediaTitle == mediaTitle && oldItem.lastEpisodeTitle == lastEpisodeTitle && oldItem.lastViewTime == lastViewTime
        }
        else -> false
    }
}