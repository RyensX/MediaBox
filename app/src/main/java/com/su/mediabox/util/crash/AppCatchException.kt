package com.su.appcrashhandler

import android.content.Context
import android.content.Intent
import com.su.mediabox.util.crash.CrashActivity
import kotlin.system.exitProcess

class AppCatchException(val context: Context) : Thread.UncaughtExceptionHandler {

    //发生Crash后响应的Activity
    private var handleActivityClass: Class<*> = CrashActivity::class.java

    override fun uncaughtException(t: Thread, e: Throwable) {
        val intent = Intent(context, handleActivityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //传入错误和硬件信息
        intent.putExtra(INFO_VERSION, InfoUtil.getVersionInfo(context))
        intent.putExtra(INFO_PHONE, InfoUtil.getPhoneInfo())
        intent.putExtra(INFO_ERROR, InfoUtil.getErrorInfo(e))
        context.startActivity(intent)
        //销毁原进程
        exitProcess(0)
    }

    companion object {
        const val INFO_ERROR = "INFO_ERROR"
        const val INFO_PHONE = "INFO_PHONE"
        const val INFO_VERSION = "INFO_VERSION"

        fun bindCrashHandler(
            context: Context,
            bindStrategy: (() -> Boolean)? = null
        ): AppCatchException? {
            if (bindStrategy?.invoke() != false)
                return AppCatchException(context)
                    .also { Thread.setDefaultUncaughtExceptionHandler(it) }
            return null
        }
    }
}