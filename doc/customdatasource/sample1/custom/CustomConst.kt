package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.model.interfaces.IConst

class CustomConst : IConst {
    override val actionUrl = ActionUrl()

    class ActionUrl : IConst.IActionUrl {
        override fun ANIME_RANK(): String = "/top/"
        override fun ANIME_PLAY(): String = "/v/"
        override fun ANIME_DETAIL(): String = "/show/"
        override fun ANIME_SEARCH(): String = "/search/"
    }

    override fun MAIN_URL(): String = BuildConfig.MAIN_URL

    override fun versionName(): String = "1.0.2"

    override fun versionCode(): Int = 3

    override fun about(): String {
        return "数据来源：${MAIN_URL()}"
    }
}
