package com.skyd.imomoe.view.component.bannerview.indicator

import android.view.View

/**
 * Created by Sky_D on 2021-02-08.
 */
interface Indicator {
    fun getView(): View?

    fun onChanged(itemCount: Int, currentPosition: Int)

    fun onPageSelected(position: Int)

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageScrollStateChanged(state: Int)
}