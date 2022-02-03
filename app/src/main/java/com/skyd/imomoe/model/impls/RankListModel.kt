package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IRankListModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

class RankListModel : IRankListModel {
    override suspend fun getRankListData(partUrl: String): Pair<MutableList<AnimeCoverBean>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }
}
