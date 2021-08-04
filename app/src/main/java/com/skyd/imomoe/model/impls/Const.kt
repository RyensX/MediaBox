package com.skyd.imomoe.model.impls

import com.skyd.imomoe.model.interfaces.IConst

class Const : IConst {
    private val actionUrl: IConst.IActionUrl = ActionUrl()

    class ActionUrl : IConst.IActionUrl {
        override fun ANIME_TOP(): String = "/top/"
        override fun ANIME_PLAY(): String = "/v/"
        override fun ANIME_DETAIL(): String = "/show/"
        override fun ANIME_SEARCH(): String = "/search/"
    }

    override fun MAIN_URL(): String? = "http://www.yhdm.so"

    override fun getActionUrl(): IConst.IActionUrl = actionUrl
}
