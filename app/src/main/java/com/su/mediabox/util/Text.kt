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

    /**
     * 从字符串中提取第一个数字，只支持阿拉伯数字
     *
     * 没有任何数字的优先放到后面
     */
    fun String.getNum(): Int {
        var isNum = false
        var num = 1
        var hasNum = false
        for (c in toCharArray())
            if (c.isDigit()) {
                isNum = true
                num += c.digitToInt()
                hasNum = true
            } else if (isNum)
                break
        if (!hasNum)
            num += length
        return num
    }

    /**
     * 格式化合成字符串，生成类似"1 - 2 - 3"这样的字符串，空白或者null不会加上多余分隔符
     */
    fun formatMergedStr(delimiter: String, vararg strs: String?) = StringBuilder().apply {
        for (str in strs)
            if (!str.isNullOrBlank())
                append(str).append(delimiter)
    }.removeSuffix(delimiter).toString()
}