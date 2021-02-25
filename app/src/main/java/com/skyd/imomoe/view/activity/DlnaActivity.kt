package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.getRedirectUrl
import com.skyd.imomoe.util.dlna.dmc.DLNACastManager
import com.skyd.imomoe.util.dlna.dmc.OnDeviceRegistryListener
import com.skyd.imomoe.view.adapter.UpnpAdapter
import com.skyd.imomoe.viewmodel.UpnpViewModel
import kotlinx.android.synthetic.main.activity_dlna.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.fourthline.cling.model.meta.Device

class DlnaActivity : AppCompatActivity() {
    private lateinit var viewModel: UpnpViewModel
    private lateinit var adapter: UpnpAdapter
    lateinit var title: String
    lateinit var url: String
    private val deviceRegistryListener = object : OnDeviceRegistryListener {
        override fun onDeviceRemoved(device: Device<*, *, *>?) {
            if (viewModel.deviceList.contains(device)) {
                viewModel.deviceList.remove(device)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onDeviceAdded(device: Device<*, *, *>?) {
            if (!viewModel.deviceList.contains(device)) {
                viewModel.deviceList.add(device)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onDeviceUpdated(device: Device<*, *, *>?) {
        }

    }

    companion object {
        const val TAG = "DlnaActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dlna)

        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""

        Log.i(TAG, url)

        viewModel = ViewModelProvider(this).get(UpnpViewModel::class.java)
        adapter = UpnpAdapter(this, viewModel.deviceList)

        tv_toolbar_1_title.text = getString(R.string.play_on_tv)
        iv_toolbar_1_back.setOnClickListener { finish() }

        rv_dlna_activity_device.layoutManager = LinearLayoutManager(this)
        rv_dlna_activity_device.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            url = getRedirectUrl(this@DlnaActivity.url)

            DLNACastManager.getInstance().registerDeviceListener(deviceRegistryListener)
            DLNACastManager.getInstance().search(DLNACastManager.DEVICE_TYPE_DMR)
        }
    }

    override fun onStart() {
        super.onStart()
        DLNACastManager.getInstance().bindCastService(this)
    }

    override fun onStop() {
        DLNACastManager.getInstance().bindCastService(this)
        super.onStop()
    }

    override fun onDestroy() {
        DLNACastManager.getInstance().unregisterListener(deviceRegistryListener)
        super.onDestroy()
    }
}