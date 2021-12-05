package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.model.interfaces.IConst

class CustomConst : IConst {
    override val actionUrl = ActionUrl()

    class ActionUrl : IConst.IActionUrl {
        override fun ANIME_RANK(): String = "/ranklist/"
        override fun ANIME_PLAY(): String = "/vp/"
        override fun ANIME_DETAIL(): String = "/showp/"
        override fun ANIME_SEARCH(): String = "/s_all"
        fun ANIME_LINK(): String = "/link/"
    }

    override fun MAIN_URL(): String = BuildConfig.CUSTOM_DATA_MAIN_URL

    override fun versionName(): String = "1.0.2"

    override fun versionCode(): Int = 3

    override fun about(): String {
        return "数据来源：${MAIN_URL()}"
    }
}
