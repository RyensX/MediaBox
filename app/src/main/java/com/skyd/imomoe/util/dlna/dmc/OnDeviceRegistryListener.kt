package com.skyd.imomoe.util.dlna.dmc

import org.fourthline.cling.model.meta.Device

/**
 * this listener call in UI thread.
 */
interface OnDeviceRegistryListener {
    fun onDeviceAdded(device: Device<*, *, *>)
    fun onDeviceUpdated(device: Device<*, *, *>)
    fun onDeviceRemoved(device: Device<*, *, *>)
}

// use kotlin dsl to replace OnDeviceRegistryListener interface
private val listeners: HashMap<OnDeviceRegistryListenerDsl.() -> Unit, OnDeviceRegistryListenerDsl>
        by lazy { HashMap() }

fun DLNACastManager.registerDeviceListener(init: OnDeviceRegistryListenerDsl.() -> Unit) {
    val listener = OnDeviceRegistryListenerDsl()
    listeners[init] = listener
    listener.init()
    this.registerDeviceListener(listener)
}

fun DLNACastManager.unregisterListener(init: OnDeviceRegistryListenerDsl.() -> Unit) {
    val listener = listeners[init]
    if (listener != null) this.unregisterListener(listener)
}

private typealias DeviceAdded = (device: Device<*, *, *>) -> Unit
private typealias DeviceUpdated = (device: Device<*, *, *>) -> Unit
private typealias DeviceRemoved = (device: Device<*, *, *>) -> Unit

class OnDeviceRegistryListenerDsl : OnDeviceRegistryListener {
    private var deviceAdded: DeviceAdded? = null
    private var deviceUpdated: DeviceUpdated? = null
    private var deviceRemoved: DeviceRemoved? = null

    fun onDeviceAdded(deviceAdded: DeviceAdded?) {
        this.deviceAdded = deviceAdded
    }

    fun onDeviceUpdated(deviceUpdated: DeviceUpdated?) {
        this.deviceUpdated = deviceUpdated
    }

    fun onDeviceRemoved(deviceRemoved: DeviceRemoved?) {
        this.deviceRemoved = deviceRemoved
    }

    override fun onDeviceAdded(device: Device<*, *, *>) {
        deviceAdded?.invoke(device)
    }

    override fun onDeviceUpdated(device: Device<*, *, *>) {
        deviceUpdated?.invoke(device)
    }

    override fun onDeviceRemoved(device: Device<*, *, *>) {
        deviceRemoved?.invoke(device)
    }
}
