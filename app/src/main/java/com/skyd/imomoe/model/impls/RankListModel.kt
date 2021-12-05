package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.interfaces.IRankListModel

class RankListModel : IRankListModel {
    override suspend fun getRankListData(partUrl: String): Pair<MutableList<AnimeCoverBean>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }
}
