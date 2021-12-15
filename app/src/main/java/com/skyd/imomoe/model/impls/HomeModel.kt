package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.interfaces.IHomeModel

class HomeModel : IHomeModel {
    override suspend fun getAllTabData(): ArrayList<TabBean> {
        return arrayListOf(TabBean("", "", "", "请设置自定义数据源"))
    }
}