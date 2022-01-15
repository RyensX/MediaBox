package com.skyd.imomoe.util.dlna.dmc.control

import org.fourthline.cling.controlpoint.ControlPoint
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IServiceActionCallback
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.IControl
import com.skyd.imomoe.util.dlna.dmc.ICast
import com.skyd.imomoe.util.dlna.dmc.Utils
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.CastEventListener
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.ServiceAction
import com.skyd.imomoe.util.dlna.dmc.control.IServiceFactory.ServiceFactoryImpl
import org.fourthline.cling.model.meta.Device

class ControlImpl(
    val controlPoint: ControlPoint,
    val device: Device<*, *, *>,
    val callbackMap: Map<String, IServiceActionCallback<*>>
) : IControl {
    private val serviceFactory: IServiceFactory

    override fun cast(device: Device<*, *, *>, `object`: ICast) {
        serviceFactory.avService.cast(object : CastEventListener {
            override fun onSuccess(result: String) {
                val listener = getCallback<Any>(ServiceAction.CAST)
                listener?.onSuccess(result)
            }

            override fun onFailed(errMsg: String) {
                val listener = getCallback<Any>(ServiceAction.CAST)
                listener?.onFailed(errMsg)
            }
        }, `object`.uri, Utils.getMetadata(`object`))
    }

    override fun isCasting(device: Device<*, *, *>): Boolean {
        return this.device == device
    }

    override fun stop() {
        serviceFactory.avService.stop(getCallback(ServiceAction.STOP))
    }

    override fun play() {
        serviceFactory.avService.play(getCallback(ServiceAction.PLAY))
    }

    override fun pause() {
        serviceFactory.avService.pause(getCallback(ServiceAction.PAUSE))
    }

    override fun seekTo(position: Long) {
        serviceFactory.avService.seek(getCallback(ServiceAction.SEEK_TO), position)
    }

    override fun setVolume(percent: Int) {
        serviceFactory.renderService.setVolume(getCallback(ServiceAction.SET_VOLUME), percent)
    }

    override fun setMute(mute: Boolean) {
        serviceFactory.renderService.setMute(getCallback(ServiceAction.SET_MUTE), mute)
    }

    override fun setBrightness(percent: Int) {
        serviceFactory.renderService
            .setBrightness(getCallback(ServiceAction.SET_BRIGHTNESS), percent)
    }

    private fun <T> getCallback(action: ServiceAction): IServiceActionCallback<T>? {
        val result = callbackMap[action.name] ?: return null
        return result as IServiceActionCallback<T>
    }

    init {
        serviceFactory = ServiceFactoryImpl(controlPoint, device)
    }
}