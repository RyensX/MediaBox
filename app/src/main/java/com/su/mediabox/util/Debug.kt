package com.su.mediabox.util

import com.su.mediabox.BuildConfig
import java.lang.StringBuilder

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

/**
 * 格式化打印出一个对象内部所有成员，建议仅在debug使用
 */
fun Any.formatMemberField(delimiter: String = " "): String {
    val format = StringBuilder("${javaClass.simpleName}(@${hashCode()}){")
    javaClass.declaredFields
        .fold(format) { sb, field ->
            sb.apply {
                field.isAccessible = true
                append("${field.name}=${field[this@formatMemberField]}$delimiter")
                field.isAccessible = false
            }
        }
    format.append("}")
    return format.toString()
}