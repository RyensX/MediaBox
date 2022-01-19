package com.skyd.imomoe.util

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset


fun InputStream.string(charset: Charset = Charsets.UTF_8): String {
    val outputStream = ByteArrayOutputStream()
    var len: Int
    val buffer = ByteArray(1024)
    while (read(buffer).also { len = it } != -1) {
        outputStream.write(buffer, 0, len)
    }
    close()
    outputStream.close()
    return String(outputStream.toByteArray(), charset)
}