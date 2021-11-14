package com.skyd.imomoe.util

import okio.ByteString
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun File.toMD5(): String? {
    var bi: BigInteger? = null
    try {
        val buffer = ByteArray(8192)
        var len: Int
        val md = MessageDigest.getInstance("MD5")
        val fis = FileInputStream(this)
        while (fis.read(buffer).also { len = it } != -1) {
            md.update(buffer, 0, len)
        }
        fis.close()
        val b = md.digest()
        bi = BigInteger(1, b)
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bi?.toString(16)
}

fun String.toMD5(): String {
    try {
        val messageDigest = MessageDigest.getInstance("MD5")
        val md5bytes = messageDigest.digest(toByteArray(charset("UTF-8")))
        return ByteString.of(*md5bytes).hex()
    } catch (e: NoSuchAlgorithmException) {
        throw AssertionError(e)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}
