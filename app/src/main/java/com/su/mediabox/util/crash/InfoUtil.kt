package com.su.appcrashhandler

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

object InfoUtil {

    /**
     * 获取错误信息
     */
    fun getErrorInfo(arg1: Throwable): String {
        val writer: Writer = StringWriter()
        val pw = PrintWriter(writer)
        arg1.printStackTrace(pw)
        pw.close()
        return writer.toString()
    }


    /**
     * 通过反射获取系统的硬件信息
     *
     * @return
     */
    fun getPhoneInfo(): String {
        val sb = StringBuffer()
        //通过反射获取系统的硬件信息
        try {
            val fields =
                Build::class.java.declaredFields
            for (field in fields) {
                field.isAccessible = true
                val name = field.name
                val value = field[null]?.toString()
                sb.append("$name=$value").append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    /**
     * 程序版本信息
     *
     * @return
     */
    fun getVersionInfo(context: Context): String? {
        return try {
            val packageInfo = context
                .packageManager
                .getPackageInfo(context.packageName, 0)
            val versionInfo = StringBuilder()
            versionInfo.append(packageInfo.versionName)
                .append("(")
                .append(packageInfo.versionCode)
                .append(")")
            versionInfo.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}