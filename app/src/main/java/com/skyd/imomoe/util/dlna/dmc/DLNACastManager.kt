package com.skyd.imomoe.util.dlna.dmc

import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.IControl
import org.fourthline.cling.android.AndroidUpnpService
import com.skyd.imomoe.util.dlna.dmc.ILogger.DefaultLoggerImpl
import android.os.Looper
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IServiceActionCallback
import org.fourthline.cling.model.types.DeviceType
import com.skyd.imomoe.util.dlna.dmc.control.ControlImpl
import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.IBinder
import com.skyd.imomoe.util.dlna.dms.MediaServer
import org.fourthline.cling.model.message.header.STAllHeader
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.CastEventListener
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.PlayEventListener
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.PauseEventListener
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.StopEventListener
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.SeekToEventListener
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.GetInfoListener
import com.skyd.imomoe.util.dlna.dmc.QueryRequest.MediaInfoRequest
import com.skyd.imomoe.util.dlna.dmc.QueryRequest.PositionInfoRequest
import com.skyd.imomoe.util.dlna.dmc.QueryRequest.TransportInfoRequest
import com.skyd.imomoe.util.dlna.dmc.QueryRequest.VolumeInfoRequest
import com.skyd.imomoe.util.showToast
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.model.types.ServiceType
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import java.util.ArrayList
import java.util.LinkedHashMap

class DLNACastManager private constructor() : IControl, OnDeviceRegistryListener {
    var dlnaCastService: AndroidUpnpService? = null
        private set
    private val mLogger: ILogger = DefaultLoggerImpl(this)
    private val mDeviceRegistryImpl = DeviceRegistryImpl(this)
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val mActionEventCallbackMap: MutableMap<String, IServiceActionCallback<*>> =
        LinkedHashMap()
    private var mSearchDeviceType: DeviceType? = null
    private var mControlImpl: ControlImpl? = null

    fun bindCastService(context: Context) {
        if (context is Application || context is Activity) {
            context.bindService(
                Intent(context, DLNACastService::class.java),
                mServiceConnection,
                Service.BIND_AUTO_CREATE
            )
        } else {
            mLogger.e("bindCastService only support Application or Activity implementation.")
        }
    }

    fun unbindCastService(context: Context) {
        if (context is Application || context is Activity) {
            context.unbindService(mServiceConnection)
        } else {
            mLogger.e("bindCastService only support Application or Activity implementation.")
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val upnpService = iBinder as AndroidUpnpService
            if (dlnaCastService !== upnpService) {
                dlnaCastService = upnpService
                Utils.logServiceConnected(mLogger, upnpService, componentName, iBinder)
                val registry = upnpService.registry
                // add registry listener
                val collection = registry.listeners
                if (collection == null || !collection.contains(mDeviceRegistryImpl)) {
                    registry.addListener(mDeviceRegistryImpl)
                }
                // Now add all devices to the list we already know about
                mDeviceRegistryImpl.setDevices(upnpService.registry.devices as Collection<Device<*, *, *>>)
            }
            val device = _mediaServer?.device
            if (device != null) {
                dlnaCastService?.registry?.addDevice(device)
            }
            _mediaServer = null
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mLogger.w(String.format("[%s] onServiceDisconnected", componentName.shortClassName))
            removeRegistryListener()
        }

        override fun onBindingDied(componentName: ComponentName) {
            mLogger.e(String.format("[%s] onBindingDied", componentName.className))
            removeRegistryListener()
        }

        private fun removeRegistryListener() {
            dlnaCastService?.registry?.removeListener(mDeviceRegistryImpl)
            dlnaCastService = null
        }
    }

    // -----------------------------------------------------------------------------------------
    // ---- register or unregister device listener
    // -----------------------------------------------------------------------------------------
    private val mLock = ByteArray(0)
    private val mRegisterDeviceListeners: MutableList<OnDeviceRegistryListener> = ArrayList()
    fun registerDeviceListener(listener: OnDeviceRegistryListener) {
//        if (listener == null) return
        dlnaCastService?.let {
            val devices: Collection<Device<*, *, *>> = if (mSearchDeviceType == null) {
                it.registry.devices
            } else {
                it.registry.getDevices(mSearchDeviceType)
            }

            // if some devices has been found, notify first.
            if (devices.isNotEmpty()) {
                exeActionInUIThread { for (device in devices) listener.onDeviceAdded(device) }
            }
        }
        synchronized(mLock) {
            if (!mRegisterDeviceListeners.contains(listener)) {
                mRegisterDeviceListeners.add(listener)
            }
        }
    }

    private fun exeActionInUIThread(action: Runnable) {
        if (Thread.currentThread() !== Looper.getMainLooper().thread) {
            mMainHandler.post(action)
        } else {
            action.run()
        }
    }

    fun unregisterListener(listener: OnDeviceRegistryListener) {
        synchronized(mLock) { mRegisterDeviceListeners.remove(listener) }
    }

    override fun onDeviceAdded(device: Device<*, *, *>) {
        if (checkDeviceType(device)) {
            synchronized(mLock) {
                for (listener in mRegisterDeviceListeners) listener.onDeviceAdded(device)
            }
        }
    }


    override fun onDeviceUpdated(device: Device<*, *, *>) {
        if (checkDeviceType(device)) {
            synchronized(mLock) {
                for (listener in mRegisterDeviceListeners) listener.onDeviceUpdated(device)
            }
        }
    }

    override fun onDeviceRemoved(device: Device<*, *, *>) {
        if (checkDeviceType(device)) {
            // if this device is casting, disconnect first!
            if (mControlImpl?.isCasting(device) == true) {
                //TODO
                // mControlImpl.release();
            }
            synchronized(mLock) {
                for (listener in mRegisterDeviceListeners) listener.onDeviceRemoved(device)
            }
        }
    }

    private fun checkDeviceType(device: Device<*, *, *>): Boolean {
        return mSearchDeviceType == null || mSearchDeviceType == device.type
    }

    // -----------------------------------------------------------------------------------------
    // ---- MediaServer
    // -----------------------------------------------------------------------------------------
    // _mediaServer会被设置为null
    private var _mediaServer: MediaServer? = null

    // mMediaServer不会被设置为null
    var mediaServer: MediaServer? = null
        private set

    fun addMediaServer(mediaServer: MediaServer) {
        val service = dlnaCastService
        if (service != null) {
            if (service.registry.getDevice(mediaServer.device.identity.udn, true) == null) {
                service.registry.addDevice(mediaServer.device)
            }
        } else {
            _mediaServer = mediaServer
        }
        this.mediaServer = mediaServer
    }

    // -----------------------------------------------------------------------------------------
    // ---- search
    // -----------------------------------------------------------------------------------------
    fun search(type: DeviceType?, maxSeconds: Int) {
        mSearchDeviceType = type
        dlnaCastService?.let { service ->
            val upnpService = service.get()
            //TODO: clear all devices first? check!!!
            upnpService.registry.removeAllRemoteDevices()
            upnpService.controlPoint.search(type?.let { UDADeviceTypeHeader(it) }
                ?: STAllHeader(), maxSeconds)
        }
    }

    fun search(type: DeviceType?) {
        mSearchDeviceType = type
        dlnaCastService?.let { service ->
            val upnpService = service.get()
            //TODO: clear all devices first? check!!!
            upnpService.registry.removeAllRemoteDevices()
            upnpService.controlPoint.search(type?.let { UDADeviceTypeHeader(it) } ?: STAllHeader())
        }
    }

    // -----------------------------------------------------------------------------------------
    // ---- action
    // -----------------------------------------------------------------------------------------
    override fun cast(device: Device<*, *, *>, `object`: ICast) {
        // check device has been connected.
        mControlImpl?.stop()
        //FIXME: cast same video should not stop and restart!
        try {
            ControlImpl(dlnaCastService!!.controlPoint, device, mActionEventCallbackMap).let {
                mControlImpl = it
                it.cast(device, `object`)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            e.message?.showToast()
        }
    }

    override fun play() {
        mControlImpl?.play()
    }

    override fun pause() {
        mControlImpl?.pause()
    }

    override fun isCasting(device: Device<*, *, *>): Boolean {
        return mControlImpl.let { it != null && it.isCasting(device) }
    }

    override fun stop() {
        mControlImpl?.stop()
    }

    override fun seekTo(position: Long) {
        mControlImpl?.seekTo(position)
    }

    override fun setVolume(percent: Int) {
        mControlImpl?.setVolume(percent)
    }

    override fun setMute(mute: Boolean) {
        mControlImpl?.setMute(mute)
    }

    override fun setBrightness(percent: Int) {
        mControlImpl?.setBrightness(percent)
    }

    fun registerActionCallbacks(vararg callbacks: IServiceActionCallback<*>) {
        innerRegisterActionCallback(*callbacks)
    }

    fun unregisterActionCallbacks() {
        if (mActionEventCallbackMap.isNotEmpty()) {
            mActionEventCallbackMap.clear()
        }
    }

    private fun innerRegisterActionCallback(vararg callbacks: IServiceActionCallback<*>) {
        if (callbacks.isNotEmpty()) {
            for (c in callbacks) {
                when (c) {
                    is CastEventListener ->
                        mActionEventCallbackMap[IServiceAction.ServiceAction.CAST.name] = c
                    is PlayEventListener ->
                        mActionEventCallbackMap[IServiceAction.ServiceAction.PLAY.name] = c
                    is PauseEventListener ->
                        mActionEventCallbackMap[IServiceAction.ServiceAction.PAUSE.name] = c
                    is StopEventListener ->
                        mActionEventCallbackMap[IServiceAction.ServiceAction.STOP.name] = c
                    is SeekToEventListener ->
                        mActionEventCallbackMap[IServiceAction.ServiceAction.SEEK_TO.name] = c
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // ---- query
    // -----------------------------------------------------------------------------------------
    fun getMediaInfo(device: Device<*, *, *>, listener: GetInfoListener<MediaInfo?>) {
        dlnaCastService?.apply {
            MediaInfoRequest(device.findService(SERVICE_AV_TRANSPORT)).execute(
                controlPoint, listener
            )
        }
    }

    fun getPositionInfo(device: Device<*, *, *>, listener: GetInfoListener<PositionInfo?>) {
        dlnaCastService?.apply {
            PositionInfoRequest(device.findService(SERVICE_AV_TRANSPORT)).execute(
                controlPoint, listener
            )
        }
    }

    fun getTransportInfo(device: Device<*, *, *>, listener: GetInfoListener<TransportInfo?>) {
        dlnaCastService?.apply {
            TransportInfoRequest(device.findService(SERVICE_AV_TRANSPORT)).execute(
                controlPoint, listener
            )
        }
    }

    fun getVolumeInfo(device: Device<*, *, *>, listener: GetInfoListener<Int?>) {
        dlnaCastService?.apply {
            VolumeInfoRequest(device.findService(SERVICE_RENDERING_CONTROL)).execute(
                controlPoint, listener
            )
        }
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DLNACastManager() }

        val DEVICE_TYPE_DMR: DeviceType = UDADeviceType("MediaRenderer")
        val SERVICE_AV_TRANSPORT: ServiceType = UDAServiceType("AVTransport")
        val SERVICE_RENDERING_CONTROL: ServiceType = UDAServiceType("RenderingControl")
        val SERVICE_CONNECTION_MANAGER: ServiceType = UDAServiceType("ConnectionManager")
    }
}