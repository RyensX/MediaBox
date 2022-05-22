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

fun <T> T.getRawClass(): Class<T>? =
    Util.withoutExceptionGet {
        Any::class.java.getDeclaredMethod("getClass").invoke(this) as Class<T>
    }

object JavaBoxClass {
    val Integer: Class<*> = Class.forName("java.lang.Integer")
    val Boolean: Class<*> = Class.forName("java.lang.Boolean")
    val Long: Class<*> = Class.forName("java.lang.Long")
    val Float: Class<*> = Class.forName("java.lang.Float")
    val Double: Class<*> = Class.forName("java.lang.Double")
    val Byte: Class<*> = Class.forName("java.lang.Byte")
    val Short: Class<*> = Class.forName("java.lang.Short")
    val Character: Class<*> = Class.forName("java.lang.Character")
}