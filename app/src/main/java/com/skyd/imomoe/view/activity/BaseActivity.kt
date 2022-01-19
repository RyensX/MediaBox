package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.skyd.skin.core.SkinBaseActivity
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.visible
import com.skyd.skin.core.SkinResourceProcessor
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity<VB : ViewBinding> : SkinBaseActivity() {
    protected lateinit var mBinding: VB
    private lateinit var loadFailedTipView: View
    private lateinit var tvImageTextTip1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(SkinResourceProcessor.instance.getSkinThemeId(R.style.Theme_Anime_skin))
        mBinding = getBinding()
        setContentView(mBinding.root)
        setColorStatusBar(window, getResColor(R.color.main_color_2_skin))
    }

    override fun onChangeSkin() {
        super.onChangeSkin()
        setTheme(SkinResourceProcessor.instance.getSkinThemeId(R.style.Theme_Anime_skin))
    }

    override fun onChangeStatusBarSkin() {
        setColorStatusBar(window, getResColor(R.color.main_color_2_skin))
    }

    protected abstract fun getBinding(): VB

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