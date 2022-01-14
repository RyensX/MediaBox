package com.skyd.imomoe.util

/**
 * 只拼接百分号%
 */
inline val Int.percentage: String
    get() = "${this}%"

/**
 * 乘100后拼接百分号
 */
fun Int.toPercentage(): String = "${this * 100}%"