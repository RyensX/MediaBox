package com.su.mediabox.util

import kotlin.jvm.Throws

@Suppress("UNCHECKED_CAST")
@Throws(Exception::class)
fun <T> Class<*>.geMember(name: String, obj: Any? = null) = getDeclaredField(name).run {
    isAccessible = true
    get(obj) as T
}

fun <T> Class<*>.geMemberOrNull(name: String, obj: Any? = null) =
    Util.withoutExceptionGet { geMember<T>(name, obj) }