package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.plugin.interfaces.IConst

object CustomConst : IConst {

    const val ANIME_RANK = "/top/"
    const val ANIME_PLAY = "/v/"
    const val ANIME_DETAIL = "/show/"
    const val ANIME_SEARCH = "/search/"

    override fun ID(): String = "com.su.mediabox.plugin.sample1"

    override fun MAIN_URL(): String = "http://www.yinghuacd.com"

    override fun versionName(): String = "1.1.0"

    override fun versionCode(): Int = 3

    override fun about(): String {
        return "数据来源：${MAIN_URL()}"
    }
}
