package com.skyd.imomoe.util.dlna.dmc

import android.os.Handler
import org.fourthline.cling.registry.DefaultRegistryListener
import com.skyd.imomoe.util.dlna.dmc.ILogger.DefaultLoggerImpl
import android.os.Looper
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.registry.Registry
import java.lang.Exception


internal class DeviceRegistryImpl(private val mOnDeviceRegistryListener: OnDeviceRegistryListener) :
    DefaultRegistryListener() {
    private val mLogger: ILogger = DefaultLoggerImpl(this)
    private val mHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var mIgnoreUpdate = true

    fun setIgnoreUpdateEvent(ignoreUpdate: Boolean) {
        mIgnoreUpdate = ignoreUpdate
    }

    fun setDevices(collection: Collection<Device<*, *, *>>?) {
        if (collection != null && collection.isNotEmpty()) {
            for (device in collection) {
                notifyDeviceAdd(device)
            }
        }
    }

    // Discovery performance optimization for very slow Android devices!
    // This function will called early than 'remoteDeviceAdded',but the device services maybe not entirely.
    override fun remoteDeviceDiscoveryStarted(registry: Registry?, device: RemoteDevice?) {
        mLogger.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        mLogger.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        mLogger.i(String.format("[%s] discovery started...", device?.details?.friendlyName))
    }

    //End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz)
    override fun remoteDeviceDiscoveryFailed(
        registry: Registry?,
        device: RemoteDevice?,
        ex: Exception?
    ) {
        mLogger.e("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        mLogger.e("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        mLogger.e(String.format("[%s] discovery failed...", device?.details?.friendlyName))
        mLogger.e(ex.toString())
    }

    // remote device
    override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
        mLogger.i(
            "remoteDeviceAdded: ${
                if (device == null) "device is null!!!" else Utils.parseDeviceInfo(device)
            }"
        )
        mLogger.i(if (device == null) "device is null!!!" else Utils.parseDeviceService(device))
        device ?: return
        notifyDeviceAdd(device)
    }

    override fun remoteDeviceUpdated(registry: Registry?, device: RemoteDevice?) {
        if (!mIgnoreUpdate) {
            mLogger.d(
                "remoteDeviceUpdated: ${
                    if (device == null) "device is null!!!" else Utils.parseDeviceInfo(device)
                }"
            )
            device ?: return
            notifyDeviceUpdate(device)
        }
    }

    override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
        mLogger.w(
            "remoteDeviceRemoved: ${
                if (device == null) "device is null!!!" else Utils.parseDeviceInfo(device)
            }"
        )
        device ?: return
        notifyDeviceRemove(device)
    }

    // local device
    override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
        super.localDeviceAdded(registry, device)
    }

    override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
        super.localDeviceRemoved(registry, device)
    }

    private fun notifyDeviceAdd(device: Device<*, *, *>) {
        mHandler.post { mOnDeviceRegistryListener.onDeviceAdded(device) }
    }

    private fun notifyDeviceUpdate(device: Device<*, *, *>) {
        mHandler.post { mOnDeviceRegistryListener.onDeviceUpdated(device) }
    }

    private fun notifyDeviceRemove(device: Device<*, *, *>) {
        mHandler.post { mOnDeviceRegistryListener.onDeviceRemoved(device) }
    }

    init {
        setIgnoreUpdateEvent(true)
    }
}