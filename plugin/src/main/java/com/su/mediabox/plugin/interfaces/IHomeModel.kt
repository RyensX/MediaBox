package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.TabBean

/**
 * 获取首页数据的接口
 */
interface IHomeModel : IBase {
    /**
     * 获取首页上方Tab数据
     *
     * @return ArrayList，不可为null。所有Tab的ArrayList
     */
    suspend fun getAllTabData(): ArrayList<TabBean>

    companion object {
        const val implName = "HomeModel"
    }
}