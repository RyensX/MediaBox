package com.skyd.imomoe.model.impls

import android.app.Activity
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IPlayModel

class PlayModel : IPlayModel {

    override suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> {
        return Triple(
            ArrayList(), ArrayList(), PlayBean(
                "", "",
                AnimeTitleBean("", "", ""),
                AnimeEpisodeDataBean("", "", ""),
                ArrayList()
            )
        )
    }

    override suspend fun refreshAnimeEpisodeData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Boolean {
        return true
    }

    override suspend fun getAnimeCoverImageBean(detailPartUrl: String): ImageBean? {
        return null
    }

    override fun setActivity(activity: Activity) {
    }

    override fun clearActivity() {
    }

    override suspend fun getAnimeEpisodeUrlData(partUrl: String): String? {
        return null
    }

}