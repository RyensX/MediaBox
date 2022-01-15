package com.skyd.imomoe.util.dlna.dmc

import android.os.Handler
import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface.GetInfoListener
import com.skyd.imomoe.util.dlna.dmc.ILogger.DefaultLoggerImpl
import android.os.Looper
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume

internal abstract class QueryRequest<T>(protected val service: Service<*, *>) {
    private var listener: GetInfoListener<T>? = null
    private val logger: ILogger by lazy { DefaultLoggerImpl(this) }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    protected abstract val actionName: String
    protected abstract val action: ActionCallback

    protected fun setResult(t: T) {
        listener?.let {
            if (Thread.currentThread() !== Looper.getMainLooper().thread) {
                mainHandler.post { it.onGetInfoResult(t, null) }
            } else {
                it.onGetInfoResult(t, null)
            }
        }
    }

    protected fun setError(errorMsg: String?) {
        logger.e(errorMsg ?: "error")
        listener?.let {
            if (Thread.currentThread() !== Looper.getMainLooper().thread) {
                mainHandler.post { it.onGetInfoResult(null, errorMsg ?: "error") }
            } else {
                it.onGetInfoResult(null, errorMsg ?: "error")
            }
        }
    }

    fun execute(point: ControlPoint, listener: GetInfoListener<T>) {
        this.listener = listener
        when {
            actionName.isBlank() -> {
                setError("not find action name!")
                return
            }
            service.getAction(actionName) == null -> {
                setError(String.format("this service not support '%s' action.", actionName))
                return
            }
            else -> {
                point.execute(action)
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---- MediaInfo
    // ---------------------------------------------------------------------------------------------
    internal class MediaInfoRequest(service: Service<*, *>) : QueryRequest<MediaInfo?>(service) {
        override val actionName: String
            get() = "GetMediaInfo"
        override val action: ActionCallback
            get() = object : GetMediaInfo(service) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = setError(defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    mediaInfo: MediaInfo?
                ) = setResult(mediaInfo)
            }
    }

    // ---------------------------------------------------------------------------------------------
    // ---- PositionInfo
    // ---------------------------------------------------------------------------------------------
    internal class PositionInfoRequest(service: Service<*, *>) :
        QueryRequest<PositionInfo?>(service) {
        override val actionName: String
            get() = "GetPositionInfo"
        override val action: ActionCallback
            get() = object : GetPositionInfo(service) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = setError(defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    positionInfo: PositionInfo?
                ) = setResult(positionInfo)
            }
    }

    // ---------------------------------------------------------------------------------------------
    // ---- TransportInfo
    // ---------------------------------------------------------------------------------------------
    internal class TransportInfoRequest(service: Service<*, *>) :
        QueryRequest<TransportInfo?>(service) {
        override val actionName: String
            get() = "GetTransportInfo"
        override val action: ActionCallback
            get() = object : GetTransportInfo(service) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = setError(defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    transportInfo: TransportInfo?
                ) = setResult(transportInfo)
            }
    }

    // ---------------------------------------------------------------------------------------------
    // ---- VolumeInfo
    // ---------------------------------------------------------------------------------------------
    internal class VolumeInfoRequest(service: Service<*, *>) : QueryRequest<Int?>(service) {
        override val actionName: String
            get() = "GetVolume"
        override val action: ActionCallback
            get() = object : GetVolume(service) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = setError(defaultMsg)

                override fun received(
                    actionInvocation: ActionInvocation<out Service<*, *>>?,
                    currentVolume: Int
                ) = setResult(currentVolume)
            }
    }
}