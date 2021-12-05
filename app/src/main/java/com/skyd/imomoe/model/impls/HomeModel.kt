package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.interfaces.IHomeModel

class HomeModel : IHomeModel {
    override suspend fun getAllTabData(): ArrayList<TabBean> {
        return arrayListOf(TabBean("", "", "", "请在设置界面选择自定义数据源Jar包以使用此APP"))
    }
}