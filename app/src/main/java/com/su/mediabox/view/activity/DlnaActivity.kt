package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.su.mediabox.databinding.ActivityDlnaBinding
import com.su.mediabox.util.Util.getRedirectUrl
import com.su.mediabox.util.dlna.Utils.isLocalMediaAddress
import com.su.mediabox.util.dlna.dmc.DLNACastManager
import com.su.mediabox.util.dlna.dmc.OnDeviceRegistryListenerDsl
import com.su.mediabox.util.dlna.dmc.registerDeviceListener
import com.su.mediabox.util.dlna.dmc.unregisterListener
import com.su.mediabox.util.logI
import com.su.mediabox.view.adapter.UpnpAdapter
import com.su.mediabox.viewmodel.UpnpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DlnaActivity : BasePluginActivity<ActivityDlnaBinding>() {
    private lateinit var viewModel: UpnpViewModel
    private lateinit var adapter: UpnpAdapter
    lateinit var title: String
    lateinit var url: String
    private val deviceRegistryListener: OnDeviceRegistryListenerDsl.() -> Unit = {
        onDeviceRemoved { device ->
            val index = viewModel.deviceList.indexOf(device)
            if (index != -1) {
                viewModel.deviceList.removeAt(index)
                adapter.notifyItemRemoved(index)
            }
        }

        onDeviceAdded { device ->
            val index = viewModel.deviceList.indexOf(device)
            if (index == -1) {
                viewModel.deviceList.add(device)
                adapter.notifyItemInserted(viewModel.deviceList.size - 1)
            }
        }
    }

    companion object {
        const val TAG = "DlnaActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""

        logI(TAG, url)

        viewModel = ViewModelProvider(this).get(UpnpViewModel::class.java)
        adapter = UpnpAdapter(this, viewModel.deviceList)

        mBinding.run {
            atbDlnaActivity.setBackButtonClickListener { finish() }

            rvDlnaActivityDevice.layoutManager = LinearLayoutManager(this@DlnaActivity)
            rvDlnaActivityDevice.adapter = adapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // 视频不是本地文件
            if (!url.isLocalMediaAddress()) {
                url = getRedirectUrl(this@DlnaActivity.url)
            }

            DLNACastManager.instance.registerDeviceListener(deviceRegistryListener)
            DLNACastManager.instance.search(DLNACastManager.DEVICE_TYPE_DMR)
        }
    }

    override fun onStart() {
        super.onStart()
        DLNACastManager.instance.bindCastService(this)
    }

    override fun onStop() {
        DLNACastManager.instance.bindCastService(this)
        super.onStop()
    }

    override fun onDestroy() {
        DLNACastManager.instance.unregisterListener(deviceRegistryListener)
        super.onDestroy()
    }

    override fun getBinding(): ActivityDlnaBinding = ActivityDlnaBinding.inflate(layoutInflater)
}