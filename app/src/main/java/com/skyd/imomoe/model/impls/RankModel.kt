package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IRankModel
import com.su.mediabox.plugin.standard.been.TabBean

class RankModel : IRankModel {
    override suspend fun getRankTabData(): ArrayList<TabBean> {
        return ArrayList()
    }
}
