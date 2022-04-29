package com.su.mediabox.util

/**
 * 查找一个值，如果不存在则填入defaultValue并返回
 *
 * @return 如果key已绑定非null，则返回该值，否则返回defaultValue
 */
fun <K, V> MutableMap<K, V>.getOrInit(key: K, defaultValue: V) =
    this[key] ?: defaultValue.also { this[key] = it }