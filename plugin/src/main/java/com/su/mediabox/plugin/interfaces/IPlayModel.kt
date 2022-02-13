package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.AnimeEpisodeDataBean
import com.su.mediabox.plugin.standard.been.IAnimeDetailBean
import com.su.mediabox.plugin.standard.been.ImageBean
import com.su.mediabox.plugin.standard.been.PlayBean

/**
 * 获取播放界面数据的接口
 */
interface IPlayModel : IBase {

    /**
     * ！！！暂未使用此方法！！！。获取弹幕数据，包括弹幕地址和发送弹幕参数。显示弹幕需重写此方法，否则无需重写
     *
     * @return HashMap<String, String>，可为null。弹幕数据HashMap
     */
    suspend fun getDanmakuData(): HashMap<String, String>? {
        return null
    }

    /**
     * 获取此部番剧封面
     *
     * @param detailPartUrl 页面相对url
     * @return ImageBean，可为null。番剧封面
     */
    suspend fun getAnimeCoverImageBean(detailPartUrl: String): ImageBean?

    /**
     * 获取播放页面相关数据
     *
     * @param partUrl              页面相对url
     * @param animeEpisodeDataBean 此集番剧数据，不为null，直接对此引用进行数据设置，不要更改此变量指向的对象
     * @return Triple，不可为null
     * ArrayList<IAnimeDetailBean>：播放页下方数据ArrayList，不为null；
     * ArrayList<AnimeEpisodeDataBean>：番剧集数列表，不为null；
     * PlayBean：此集番剧数据，不为null
     */
    suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean>

    /**
     * 获取当前页面播放视频的地址
     *
     * @param partUrl  页面相对url
     * @return String，可为null。此页面播放的视频地址
     */
    suspend fun getAnimeEpisodeUrlData(partUrl: String): String?

    /**
     * 获取传入partUrl页面对应的视频的数据
     *
     * @param partUrl              页面相对url
     * @param animeEpisodeDataBean partUrl页面对应的视频的数据Bean，不为null，直接对此变量设置数据，不要更改此变量指向的对象
     * @return Boolean，不可为null。获取成功true，否则false
     */
    suspend fun refreshAnimeEpisodeData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Boolean

    companion object {
        const val implName = "PlayModel"
    }
}