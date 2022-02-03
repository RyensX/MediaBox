package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.AnimeCoverBean

/**
 * 获取每日更新番剧桌面小组件的数据接口
 */
interface IEverydayAnimeWidgetModel : IBase {
    /**
     * 获取桌面小组件的每日番剧数据
     *
     * @return ArrayList，不可为null。每天更新番剧的ArrayList，其中共有七条
     */
    fun getEverydayAnimeData(): ArrayList<List<AnimeCoverBean>>

    companion object {
        const val implName = "EverydayAnimeWidgetModel"
    }
}