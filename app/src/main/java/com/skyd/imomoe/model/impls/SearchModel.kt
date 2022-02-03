package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.ISearchModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

class SearchModel : ISearchModel {
    override suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }
}