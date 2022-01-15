package com.skyd.imomoe.util.dlna.dmc.control

import com.skyd.imomoe.util.dlna.dmc.*
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IServiceActionCallback
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Device

interface ICastInterface {
    // ------------------------------------------------------------------
    // ---- control
    // ------------------------------------------------------------------
    interface IControl {
        fun cast(device: Device<*, *, *>, `object`: ICast)
        fun isCasting(device: Device<*, *, *>): Boolean
        fun stop()
        fun play()
        fun pause()

        /**
         * @param position, current watch time(ms)
         */
        fun seekTo(position: Long)
        fun setVolume(percent: Int)
        fun setMute(mute: Boolean)
        fun setBrightness(percent: Int)
    }

    // ------------------------------------------------------------------
    // ---- subscription
    // ------------------------------------------------------------------
    interface ISubscriptionListener {
        fun onSubscriptionEstablished(subscription: GENASubscription<*>)
        fun onSubscriptionEventReceived(subscription: GENASubscription<*>)
        fun onSubscriptionFinished(
            subscription: GENASubscription<*>,
            responseStatus: UpnpResponse,
            defaultMsg: String
        )
    }

    // ------------------------------------------------------------------
    // ---- GetInfo Listener
    // ------------------------------------------------------------------
    interface GetInfoListener<T> {
        fun onGetInfoResult(t: T?, errMsg: String?)
    }

    // ------------------------------------------------------------------
    // ---- Event Listener
    // ------------------------------------------------------------------
    interface CastEventListener : IServiceActionCallback<String>
    interface PlayEventListener : IServiceActionCallback<Void?>
    interface PauseEventListener : IServiceActionCallback<Void?>
    interface StopEventListener : IServiceActionCallback<Void?>
    interface SeekToEventListener : IServiceActionCallback<Long>
}

// use kotlin dsl to replace ICastInterface.GetInfoListener<T> interface
// 由于使用此接口的方法不止一个，因此下面的方法与平常的用法不同
fun <T> newGetInfoListener(onDeviceAdded: ((t: T?, errMsg: String?) -> Unit)): GetInfoListenerDsl<T> {
    val listener = GetInfoListenerDsl<T>()
    listener.onGetInfoResult(onDeviceAdded)
    return listener
}

class GetInfoListenerDsl<T> : ICastInterface.GetInfoListener<T> {
    private var getInfoResult: ((t: T?, errMsg: String?) -> Unit)? = null

    fun onGetInfoResult(getInfoResult: ((t: T?, errMsg: String?) -> Unit)?) {
        this.getInfoResult = getInfoResult
    }

    override fun onGetInfoResult(t: T?, errMsg: String?) {
        getInfoResult?.invoke(t, errMsg)
    }
}
