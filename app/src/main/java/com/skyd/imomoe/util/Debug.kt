package com.skyd.imomoe.util

import com.skyd.imomoe.BuildConfig

/**
 * 只有debug包才会执行表达式
 */
fun debug(lambda: () -> Unit) {
    if (BuildConfig.DEBUG) {
        lambda.invoke()
    }
}

/**
 * 只有release包才会执行表达式
 */
fun release(lambda: () -> Unit) {
    if (!BuildConfig.DEBUG) {
        lambda.invoke()
    }
}