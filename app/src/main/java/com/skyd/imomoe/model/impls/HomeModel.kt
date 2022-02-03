package com.skyd.imomoe.model.impls

import com.su.mediabox.plugin.interfaces.IHomeModel
import com.su.mediabox.plugin.standard.been.TabBean

class HomeModel : IHomeModel {
    override suspend fun getAllTabData(): ArrayList<TabBean> {
        return arrayListOf(TabBean("", "", "", "请设置自定义数据源"))
    }
}