package com.skyd.imomoe.model.impls

import android.app.Activity
import com.su.mediabox.plugin.interfaces.IPlayModel
import com.su.mediabox.plugin.standard.been.*

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