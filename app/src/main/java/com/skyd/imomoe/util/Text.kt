package com.skyd.imomoe.util

import com.skyd.imomoe.BuildConfig

object Text {
    /**
     * 屏蔽带有某些关键字的弹幕
     *
     * @return 若屏蔽此字符串，则返回true，否则false
     */
    fun String.shield(): Boolean {
        BuildConfig.SHIELD_TEXT.forEach {
            if (this.contains(it, true)) return true
        }
        return false
    }
}