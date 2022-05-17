package com.su.mediabox.view.activity

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.efs.sdk.launch.LaunchManager
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.util.Util.setColorStatusBar
import com.su.mediabox.util.eventbus.EventBusSubscriber
import com.su.mediabox.util.gone
import com.su.mediabox.util.logE
import com.su.mediabox.util.release
import com.su.mediabox.util.visible
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var mBinding: VB
    private lateinit var loadFailedTipView: View
    private lateinit var tvImageTextTip1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getBinding()
        setContentView(mBinding.root)
        setColorStatusBar(window, ContextCompat.getColor(App.context, R.color.main_color_2_skin))

        release {
            LaunchManager.onTraceApp(application, LaunchManager.APP_ON_CREATE, false)
        }
    }

    protected abstract fun getBinding(): VB

    override fun onStart() {
        super.onStart()
        if (this is EventBusSubscriber) EventBus.getDefault().register(this)
        release {
            LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_START, true)
        }
    }

    override fun onRestart() {
        super.onRestart()
        release {
            LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_RE_START, true)
        }
    }

    override fun onResume() {
        super.onResume()
        release {
            LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_RESUME, false)
        }
    }

    override fun onStop() {
        super.onStop()
        if (this is EventBusSubscriber && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

        release {
            LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_STOP, true)
        }
    }

    protected open fun getLoadFailedTipView(): ViewStub? = null

    protected open fun showLoadFailedTip(
        text: CharSequence,
        onClickListener: View.OnClickListener?
    ) {
        val loadFailedTipViewStub = getLoadFailedTipView() ?: return
        if (loadFailedTipViewStub.parent != null) {
            loadFailedTipView = loadFailedTipViewStub.inflate()
            tvImageTextTip1 = loadFailedTipView.findViewById(R.id.tv_image_text_tip_1)
            tvImageTextTip1.movementMethod = LinkMovementMethod.getInstance()
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