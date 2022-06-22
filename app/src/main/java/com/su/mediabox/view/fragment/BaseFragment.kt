package com.su.mediabox.view.fragment

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private var isFirstPagerInit = true

    /**
     * 第一次选中显示时调用
     */
    open fun pagerInit() {}

    /**
     * 每次被选中显示时调用
     */
    open fun pageSelected() {}

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (isFirstPagerInit) {
            pagerInit()
            isFirstPagerInit = false
        }
        pageSelected()
    }

}