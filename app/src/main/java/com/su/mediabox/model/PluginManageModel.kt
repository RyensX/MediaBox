package com.su.mediabox.model

import com.su.mediabox.bean.MediaFavorite

class PluginManageModel(
    val pluginInfo: PluginInfo, override val childData: List<MediaFavorite>? = null,
    val updateMediaCount: Long
) : GroupModel<MediaFavorite> {
    override var isExpand: Boolean = false
}