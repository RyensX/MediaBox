package com.su.mediabox.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingFragment<VB : ViewBinding> : BaseFragment() {

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