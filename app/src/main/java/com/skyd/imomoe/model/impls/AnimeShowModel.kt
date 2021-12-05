package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.bean.IAnimeShowBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_BROWSER
import com.skyd.imomoe.model.interfaces.IAnimeShowModel

class AnimeShowModel : IAnimeShowModel {
    override suspend fun getAnimeShowData(
        partUrl: String
    ): Pair<ArrayList<IAnimeShowBean>, PageNumberBean?> {
        return Pair(
            arrayListOf(
                AnimeShowBean(
                    com.skyd.imomoe.config.Const.ViewHolderTypeString.BANNER_1, "",
                    "", "", "", null, "",
                    arrayListOf(
                        AnimeCoverBean(
                            com.skyd.imomoe.config.Const.ViewHolderTypeString.ANIME_COVER_6,
                            ANIME_BROWSER + "https://github.com/SkyD666/Imomoe/tree/master/doc/customdatasource/README.md",
                            "https://github.com/SkyD666/Imomoe/tree/master/doc/customdatasource/README.md",
                            "具体使用方法请点击此处",
                            null,
                            ""
                        )
                    )
                )
            ), null
        )
    }
}