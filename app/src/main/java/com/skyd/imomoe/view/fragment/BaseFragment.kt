package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.skyd.imomoe.R
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.visible
import com.skyd.skin.core.SkinBaseFragment
import org.greenrobot.eventbus.EventBus


abstract class BaseFragment<VB : ViewBinding> : SkinBaseFragment() {
    protected var isFirstLoadData = true
    private var binding: VB? = null
    protected val mBinding get() = binding!!
    private lateinit var loadFailedTipView: View
    private lateinit var tvImageTextTip1: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = getBinding(inflater, container)
        this.binding = binding
        return binding.root
    }

    protected abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        if (this is EventBusSubscriber) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (this is EventBusSubscriber && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    protected open fun getLoadFailedTipView(): ViewStub? = null

    protected open fun showLoadFailedTip(text: String, onClickListener: View.OnClickListener?) {
        val loadFailedTipViewStub = getLoadFailedTipView() ?: return
        if (loadFailedTipViewStub.parent != null) {
            loadFailedTipView = loadFailedTipViewStub.inflate()
            tvImageTextTip1 = loadFailedTipView.findViewById(R.id.tv_image_text_tip_1)
            tvImageTextTip1.text = text
            if (onClickListener != null) loadFailedTipView.setOnClickListener(onClickListener)
        } else {
            if (this::loadFailedTipView.isInitialized) {
                loadFailedTipView.visible()
            } else {
                logE("showLoadFailedTip", "layout_image_text_tip_1 isn't initialized")
            }
        }
    }

    protected open fun hideLoadFailedTip() {
        val loadFailedTipViewStub = getLoadFailedTipView() ?: return
        if (loadFailedTipViewStub.parent == null) {
            if (this::loadFailedTipView.isInitialized) {
                loadFailedTipView.gone()
            } else {
                logE("showLoadFailedTip", "layout_image_text_tip_1 isn't initialized")
            }
        }
    }
}
