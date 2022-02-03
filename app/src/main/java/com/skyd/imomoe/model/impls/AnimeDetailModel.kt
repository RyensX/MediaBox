package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IAnimeDetailModel
import com.su.mediabox.plugin.standard.been.IAnimeDetailBean
import com.su.mediabox.plugin.standard.been.ImageBean

class AnimeDetailModel : IAnimeDetailModel {
    override suspend fun getAnimeDetailData(
        partUrl: String
    ): Triple<ImageBean, String, ArrayList<IAnimeDetailBean>> {
        return Triple(ImageBean("", "", "", ""), "", ArrayList())
    }
}