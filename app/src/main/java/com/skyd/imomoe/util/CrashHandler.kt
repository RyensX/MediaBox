package com.skyd.imomoe.util

import android.content.Context
import android.util.Log
import com.skyd.imomoe.view.activity.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess


class CrashHandler private constructor(val context: Context) : Thread.UncaughtExceptionHandler {
    private val mDefaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        try {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            ex.printStackTrace(printWriter)
            var cause = ex.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val unCaughtException = stringWriter.toString()  //详细错误日志
            logE("crash info", unCaughtException)
            CrashActivity.start(context, unCaughtException)
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mDefaultHandler?.uncaughtException(thread, ex)
    }

    companion object {
        private var instance: CrashHandler? = null

        /**
         * 单例
         */
        fun getInstance(context: Context): CrashHandler? {
            var inst = instance
            if (inst == null) {
                synchronized(CrashHandler::class.java) {
                    inst = instance
                    if (inst == null) {
                        inst = CrashHandler(context)
                        instance = inst
                    }
                }
            }
            return inst
        }
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}