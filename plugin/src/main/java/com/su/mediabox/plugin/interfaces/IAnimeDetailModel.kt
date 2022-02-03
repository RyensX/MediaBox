package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.IAnimeDetailBean
import com.su.mediabox.plugin.standard.been.ImageBean
import java.util.*

/**
 * 获取番剧详情数据接口
 */
interface IAnimeDetailModel : IBase {
    /**
     * 获取番剧详情页数据
     *
     * @param partUrl  页面部分url，不为null
     * @return Triple，不可为null
     * ImageBean：番剧封面图片类，不可为null；
     * String：番剧名，不可为null；
     * ArrayList<IAnimeDetailBean>：详情页数据List，不可为null
     */
    suspend fun getAnimeDetailData(partUrl: String): Triple<ImageBean, String, ArrayList<IAnimeDetailBean>>

    companion object {
        const val implName = "AnimeDetailModel"
    }
}