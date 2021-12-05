package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IEverydayAnimeWidgetModel

class EverydayAnimeWidgetModel : IEverydayAnimeWidgetModel {
    override suspend fun getEverydayAnimeData(): ArrayList<List<AnimeCoverBean>> {
        return ArrayList()
    }
}