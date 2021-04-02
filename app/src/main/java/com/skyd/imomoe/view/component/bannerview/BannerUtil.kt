package com.skyd.imomoe.view.component.bannerview

/**
 * Created by Sky_D on 2021-02-08.
 */
object BannerUtil {
    fun getPosition(pos: Int, count: Int) = if (count != 0) when (pos) {
        0 -> {
            count - 1
        }
        count + 1 -> {
            0
        }
        else -> {
            pos - 1
        }
    } else 0

    fun getRealPosition(pos: Int) = pos + 1
}