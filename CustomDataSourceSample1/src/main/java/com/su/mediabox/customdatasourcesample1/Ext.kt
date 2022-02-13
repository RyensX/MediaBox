package com.su.mediabox.customdatasourcesample1

import java.util.regex.Pattern

object Text {
    fun String.isYearMonth(): Boolean {
        return Pattern.compile("[1-9][0-9]{3}(0[1-9]|1[0-2])").matcher(this).matches()
    }
}