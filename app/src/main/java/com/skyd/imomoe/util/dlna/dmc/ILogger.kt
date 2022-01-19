package com.skyd.imomoe.util.dlna.dmc

import android.util.Log
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.util.*

interface ILogger {
    fun v(msg: String?)
    fun d(msg: String?)
    fun i(msg: String?)
    fun w(msg: String?)
    fun e(msg: String?)

    class DefaultLoggerImpl @JvmOverloads constructor(
        `object`: Any,
        debug: Boolean = BuildConfig.DEBUG
    ) : ILogger {
        private val TAG: String
        private val DEBUG: Boolean

        override fun v(msg: String?) {
            if (DEBUG) logV(TAG, msg.toString())
        }

        override fun d(msg: String?) {
            if (DEBUG) logD(TAG, msg.toString())
        }

        override fun i(msg: String?) {
            if (DEBUG) logI(TAG, msg.toString())
        }

        override fun w(msg: String?) {
            if (DEBUG) logW(TAG, msg.toString())
        }

        override fun e(msg: String?) {
            if (DEBUG) logE(TAG, msg.toString())
        }

        init {
            var className = `object`.javaClass.simpleName
            if (className.isNullOrEmpty()) {
                className = if (`object`.javaClass.superclass != null) {
                    `object`.javaClass.superclass.simpleName
                } else {
                    "$1"
                }
            }
            TAG = PREFIX_TAG + className
            DEBUG = debug
        }
    }

    companion object {
        const val PREFIX_TAG = "DLNACast_"
    }
}