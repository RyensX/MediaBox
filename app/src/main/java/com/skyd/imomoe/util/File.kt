package com.skyd.imomoe.util

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.skyd.imomoe.App
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.math.BigDecimal

val File.uri: Uri
    get() = if (Build.VERSION.SDK_INT >= 24) {
        FileProvider.getUriForFile(App.context, "com.skyd.imomoe.fileProvider", this)
    } else {
        Uri.fromFile(this)
    }

fun Uri.copyTo(target: File): File {
    App.context.contentResolver.openInputStream(this)!!.copyTo(FileOutputStream(target))
    return target
}

fun String.toFile() = File(this)

fun File.fileSize(): Long {
    var s: Long = 0
    if (this.exists() && this.isFile) {
        val fis = FileInputStream(this)
        s = fis.available().toLong()
    }
    return s
}

fun File.directorySize(): Long {
    var size: Long = 0
    val fList = listFiles()
    fList?.let {
        for (i in it.indices) {
            size += if (it[i].isDirectory) {
                it[i].directorySize()
            } else {
                it[i].fileSize()
            }
        }
    }
    return size
}

/**
 * 获取规整的文件大小
 * @param newScale 精确到小数点几位
 */
fun Double.formatSize(newScale: Int = 2): String {
    val kiloByte = this / 1024
    if (kiloByte < 1) {
        return this.toString() + "B"
    }
    val megaByte = kiloByte / 1024
    if (megaByte < 1) {
        val result1 = BigDecimal(kiloByte.toString())
        return result1.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
            .toString() + "K"
    }
    val gigaByte = megaByte / 1024
    if (gigaByte < 1) {
        val result2 = BigDecimal(megaByte.toString())
        return result2.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
            .toString() + "M"
    }
    val teraBytes = gigaByte / 1024
    if (teraBytes < 1) {
        val result3 = BigDecimal(gigaByte.toString())
        return result3.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
            .toString() + "G"
    }
    val result4 = BigDecimal(teraBytes)
    return result4.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
        .toString() + "T"
}

/**
 * 获取规整的文件大小
 * @param newScale 精确到小数点几位
 */
fun File.formatSize(newScale: Int = 2): String {
    val size: Double = if (isFile) fileSize().toDouble() else directorySize().toDouble()
    return size.formatSize(newScale)
}