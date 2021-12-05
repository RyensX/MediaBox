package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IAnimeDetailModel

class AnimeDetailModel : IAnimeDetailModel {
    override suspend fun getAnimeDetailData(
        partUrl: String
    ): Triple<ImageBean, String, ArrayList<IAnimeDetailBean>> {
        return Triple(ImageBean("", "", "", ""), "", ArrayList())
    }
}