package com.skyd.imomoe.view.component.player

import master.flame.danmaku.danmaku.loader.ILoader
import master.flame.danmaku.danmaku.loader.android.AcFunDanmakuLoader
import master.flame.danmaku.danmaku.loader.android.BiliDanmakuLoader

class AnimeDanmakuLoaderFactory {
    companion object {
        var TAG_BILI = "bili"
        var TAG_ACFUN = "acfun"
        var TAG_ANIME = "anime"
        fun create(tag: String): ILoader? {
            return when {
                TAG_BILI.equals(tag, ignoreCase = true) -> BiliDanmakuLoader.instance()
                TAG_ACFUN.equals(tag, ignoreCase = true) -> AcFunDanmakuLoader.instance()
                TAG_ANIME.equals(tag, ignoreCase = true) -> AnimeDanmakuLoader.instance
                else -> AnimeDanmakuLoader.instance
            }
        }
    }
}