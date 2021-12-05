package com.skyd.imomoe.model.impls

import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.model.interfaces.IConst

class Const : IConst {
    override val actionUrl = ActionUrl()

    class ActionUrl : IConst.IActionUrl {
        override fun ANIME_RANK(): String = ""
        override fun ANIME_PLAY(): String = ""
        override fun ANIME_DETAIL(): String = ""
        override fun ANIME_SEARCH(): String = ""
    }

    override fun MAIN_URL(): String = BuildConfig.MAIN_URL

    override fun versionName(): String = "1.0.2"

    override fun versionCode(): Int = 3

    override fun about(): String {
        return "默认数据源，不提供任何数据，请在设置界面手动选择自定义数据源！"
    }
}
