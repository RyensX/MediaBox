package com.su.mediabox.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.su.mediabox.R
import com.su.mediabox.util.eventbus.EventBusSubscriber
import com.su.mediabox.util.gone
import com.su.mediabox.util.logE
import com.su.mediabox.util.visible
import com.su.skin.core.SkinBaseFragment
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
