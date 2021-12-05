package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel

class MonthAnimeModel : IMonthAnimeModel {
    override suspend fun getMonthAnimeData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }
}