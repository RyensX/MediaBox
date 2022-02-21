package com.su.mediabox.util

import com.su.mediabox.BuildConfig

/**
 * 只有debug包才会执行表达式
 */
inline fun debug(lambda: () -> Unit) {
    if (BuildConfig.DEBUG) {
        lambda.invoke()
    }
}

/**
 * 只有release包才会执行表达式
 */
inline fun release(lambda: () -> Unit) {
    if (!BuildConfig.DEBUG) {
        lambda.invoke()
    }
}