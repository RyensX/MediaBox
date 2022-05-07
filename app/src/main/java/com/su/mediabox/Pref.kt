package com.su.mediabox

import com.chibatching.kotpref.KotprefModel
import com.su.mediabox.view.viewcomponents.*

object Pref : KotprefModel() {
    override val kotprefName: String = "App"

    /**
     * 视图组件[Cover1ViewHolder]是否显示历史播放
     */
    var videoCover1ShowHistory by booleanPref()

    /**
     * 视图组件[VideoPlayListViewHolder]是否显示历史播放
     */
    var videoPlayListShowHistory by booleanPref(true)
}