package com.skyd.imomoe.config

interface Api {
    companion object {
        const val MAIN_URL = "http://www.yhdm.io"

        //github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"
        //gitee
        const val CHECK_UPDATE_URL_2 = "https://gitee.com/api/v5/repos/SkyD666/Imomoe/releases/latest"
    }
}