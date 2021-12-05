package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IEverydayAnimeModel

class EverydayAnimeModel : IEverydayAnimeModel {
    override suspend fun getEverydayAnimeData(): Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, AnimeShowBean> {
        return Triple(
            ArrayList(), ArrayList(), AnimeShowBean(
                "", "", "", "",
                "", null, "", null
            )
        )
    }
}