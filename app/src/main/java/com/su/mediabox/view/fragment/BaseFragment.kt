package com.su.mediabox.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    /**
     * 必须要在[onCreateView]后才能调用
     */
    protected lateinit var mBinding: VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = buildViewBinding(inflater, container).apply { mBinding = this }.root

    abstract fun buildViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

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