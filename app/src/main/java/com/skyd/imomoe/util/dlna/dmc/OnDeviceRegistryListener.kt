package com.skyd.imomoe.util.dlna.dmc

import org.fourthline.cling.model.meta.Device

/**
 * this listener call in UI thread.
 */
interface OnDeviceRegistryListener {
    fun onDeviceAdded(device: Device<*, *, *>?)
    fun onDeviceUpdated(device: Device<*, *, *>?)
    fun onDeviceRemoved(device: Device<*, *, *>?)
}