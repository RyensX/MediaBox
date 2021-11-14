package com.skyd.imomoe.util

import java.io.InputStream

fun InputStream.string(): String {
    val out = StringBuffer()
    val b = ByteArray(4096)
    var n: Int
    while (this.read(b).also { n = it } != -1) {
        out.append(String(b, 0, n))
    }
    return out.toString()
}