package com.su.mediabox.util

import com.su.mediabox.App
import com.su.mediabox.R
import java.text.SimpleDateFormat
import java.util.*

private val timeFormatPool by unsafeLazy { mutableMapOf<String, SimpleDateFormat>() }

fun getDataFormat(format: String, locale: Locale = Locale.CHINA) =
    timeFormatPool[format + locale.hashCode()] ?: SimpleDateFormat(format, locale).also {
        timeFormatPool[format + locale.hashCode()] = it
    }

fun friendlyTime(timestamp: Long): String {
    val time: Long = (System.currentTimeMillis() - timestamp) / 1000L

    return when {
        time in 0 until 60 -> App.context.getString(R.string.time_format_now)
        time in 60 until 3600 ->
            App.context.getString(R.string.time_format_minutes, time / 60)
        time >= 3600 && time < 3600 * 24 ->
            App.context.getString(R.string.time_format_hour, time / 3600)
        time >= 3600 * 24 && time < 3600 * 24 * 30 -> {
            when (val day = time / 3600 / 24) {
                1L -> getDataFormat(
                    App.context.getString(R.string.time_format_yesterday)
                ).format(timestamp)
                2L -> getDataFormat(
                    App.context.getString(R.string.time_format_yesterday_yesterday)
                ).format(timestamp)
                else -> App.context.getString(R.string.time_format_in_month, day)
            }
        }
        else -> {
            val currYear =
                Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
                    .get(Calendar.YEAR)
            val tsYear =
                Calendar.getInstance().apply { timeInMillis = timestamp }.get(Calendar.YEAR)
            if (currYear == tsYear)
                getDataFormat(App.context.getString(R.string.time_format_in_year)).format(timestamp)
            else
                getDataFormat(App.context.getString(R.string.time_format_out_year)).format(timestamp)
        }
    }
}