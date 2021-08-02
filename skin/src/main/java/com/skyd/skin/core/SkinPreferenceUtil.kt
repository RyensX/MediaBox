package com.skyd.skin.core

import android.content.Context

object SkinPreferenceUtil {
    var PREFERENCE_NAME = "skinData"

    fun putString(context: Context, key: String, value: String?): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.getString(key, defaultValue)
    }

    fun putInt(context: Context, key: String, value: Int): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun getInt(context: Context, key: String, defaultValue: Int = -1): Int {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.getInt(key, defaultValue)
    }

    fun putLong(context: Context, key: String, value: Long): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = -1L): Long {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.getLong(key, defaultValue)
    }

    fun putFloat(context: Context, key: String, value: Float): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getFloat(context: Context, key: String, defaultValue: Float = -1f): Float {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.getFloat(key, defaultValue)
    }

    fun putBoolean(context: Context, key: String, value: Boolean): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.getBoolean(key, defaultValue)
    }

    fun contains(context: Context, key: String?): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return settings.contains(key)
    }

    fun clearSP(context: Context): Boolean {
        val settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
        return true
    }

    fun put(context: Context, key: String, `object`: Any) {
        when (`object`) {
            is String -> putString(context, key, `object`)
            is Int -> putInt(context, key, `object`)
            is Boolean -> putBoolean(context, key, `object`)
            is Float -> putFloat(context, key, `object`)
            is Long -> putLong(context, key, `object`)
            else -> putString(context, key, `object`.toString())
        }
    }

    fun get(context: Context, key: String, defaultObject: Any?): Any? {
        return when (defaultObject) {
            is String -> getString(context, key, defaultObject as String?)
            is Int -> getInt(context, key, defaultObject)
            is Boolean -> getBoolean(context, key, defaultObject)
            is Float -> getFloat(context, key, defaultObject)
            is Long -> getLong(context, key, defaultObject)
            else -> return null
        }
    }
}