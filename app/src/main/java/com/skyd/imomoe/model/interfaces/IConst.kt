package com.skyd.imomoe.model.interfaces

/**
 * 获取MAIN_URL、页面跳转信息、自定义数据jar包的关于信息等
 */
interface IConst : IBase {
    companion object {
        const val implName = "Const"
    }

    /**
     * @return MAIN_URL
     */
    fun MAIN_URL(): String

    /**
     * @return jar包的关于信息
     */
    fun about(): String {
        return MAIN_URL() + ""
    }

    /**
     * @return jar包的版本名信息
     */
    fun versionName(): String? {
        return null
    }

    /**
     * @return jar包的版本号信息
     */
    fun versionCode(): Int {
        return 0
    }

    /**
     * @return 跳转类实例
     */
    val actionUrl: IActionUrl

    interface IActionUrl {
        /**
         * @return 番剧详情界面跳转URL
         */
        fun ANIME_DETAIL(): String

        /**
         * @return 播放界面跳转URL
         */
        fun ANIME_PLAY(): String

        /**
         * @return 搜索界面跳转URL
         */
        fun ANIME_SEARCH(): String

        /**
         * @return 排行榜界面跳转URL
         */
        fun ANIME_RANK(): String
    }
}