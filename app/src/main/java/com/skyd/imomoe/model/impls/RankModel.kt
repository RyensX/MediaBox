package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.interfaces.IRankModel

class RankModel : IRankModel {
    override suspend fun getRankTabData(): ArrayList<TabBean> {
        return ArrayList()
    }
}
