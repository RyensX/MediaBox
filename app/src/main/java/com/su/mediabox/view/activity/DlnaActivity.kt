package com.su.mediabox.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.microsoft.appcenter.analytics.Analytics
import com.su.mediabox.R
import com.su.mediabox.bean.DefaultEmpty
import com.su.mediabox.databinding.ActivityDlnaBinding
import com.su.mediabox.databinding.ItemDlnaDevice1Binding
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.Util.getRedirectUrl
import com.su.mediabox.util.dlna.Utils.isLocalMediaAddress
import com.su.mediabox.util.dlna.dmc.DLNACastManager
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.viewBind
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder
import com.su.mediabox.viewmodel.UpnpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.RemoteDevice

class DlnaActivity : BaseActivity() {

    private val mBinding by viewBind(ActivityDlnaBinding::inflate)
    private val viewModel by viewModels<UpnpViewModel>()
    lateinit var title: String
    lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.trackEvent("功能：DLNA投屏")

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""

        mBinding.run {
            atbDlnaActivity.setBackButtonClickListener { finish() }

            rvDlnaActivityDevice.linear()
                .initTypeList(
                    DataViewMapList()
                        .registerDataViewMap<RemoteDevice, DlnaViewHolder>()
                        .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
                ) {
                    emptyData = SimpleTextData(getString(R.string.checking)).apply {
                        paddingLeft = 16.dp
                        paddingTop = 4.dp
                        paddingBottom = 4.dp
                    }
                    setTag(url, "url")
                    setTag(title, "title")
                }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // 视频不是本地文件
            if (!url.isLocalMediaAddress()) {
                url = getRedirectUrl(this@DlnaActivity.url)
            }
        }

        viewModel.deviceList.observe(this) {
            mBinding.rvDlnaActivityDevice.submitList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        DLNACastManager.instance.bindCastService(this)
    }

    override fun onStop() {
        DLNACastManager.instance.unbindCastService(this)
        super.onStop()
    }

    class DlnaViewHolder private constructor(private val binding: ItemDlnaDevice1Binding) :
        TypeViewHolder<RemoteDevice>(binding.root) {

        private var tmpData: RemoteDevice? = null

        constructor(parent: ViewGroup) : this(
            ItemDlnaDevice1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(itemView) {
                tmpData?.also {
                    val key = System.currentTimeMillis().toString()
                    DlnaControlActivity.deviceHashMap[key] = it
                    bindingContext.goActivity<DlnaControlActivity>(Intent().apply {
                        putExtra("url", bindingTypeAdapter.getTag<String>("url")!!)
                        putExtra("title", bindingTypeAdapter.getTag<String>("title")!!)
                        putExtra("deviceKey", key)
                    })
                }
            }
        }

        override fun onBind(data: RemoteDevice) {
            tmpData = data
            binding.tvUpnpDevice1Title.text = data.details?.friendlyName
        }
    }
}