package com.su.mediabox.util

object Text {
    /**
     * 屏蔽带有某些关键字的弹幕
     *
     * @return 若屏蔽此字符串，则返回true，否则false
     */
    fun String.shield(): Boolean {
        return false
    }
}