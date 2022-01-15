package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import org.fourthline.cling.model.meta.Device
import java.util.*

class UpnpViewModel : ViewModel() {
    var deviceList: MutableList<Device<*, *, *>> = ArrayList()

    companion object {
        const val TAG = "UpnpViewModel"
    }
}