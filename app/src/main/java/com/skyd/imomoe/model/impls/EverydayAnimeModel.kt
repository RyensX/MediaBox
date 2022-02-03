package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IEverydayAnimeModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.AnimeShowBean
import com.su.mediabox.plugin.standard.been.TabBean

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