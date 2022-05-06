package com.su.mediabox.viewmodel

import androidx.lifecycle.ViewModel
import com.su.mediabox.util.MutableListLiveData
import com.su.mediabox.util.dlna.dmc.DLNACastManager
import com.su.mediabox.util.dlna.dmc.OnDeviceRegistryListenerDsl
import com.su.mediabox.util.dlna.dmc.registerDeviceListener
import com.su.mediabox.util.dlna.dmc.unregisterListener
import com.su.mediabox.util.toLiveData
import org.fourthline.cling.model.meta.Device

class UpnpViewModel : ViewModel() {

    private val _deviceList = MutableListLiveData<Device<*, *, *>>()
    val deviceList = _deviceList.toLiveData()

    private val deviceRegistryListener: OnDeviceRegistryListenerDsl.() -> Unit = {
        onDeviceRemoved { device ->
            _deviceList.removeData(device)
        }

        onDeviceAdded { device ->
            _deviceList.addData(device)
        }
    }

    init {
        DLNACastManager.instance.registerDeviceListener(deviceRegistryListener)
        DLNACastManager.instance.search(DLNACastManager.DEVICE_TYPE_DMR)
    }

    override fun onCleared() {
        DLNACastManager.instance.unregisterListener(deviceRegistryListener)
        super.onCleared()
    }
}