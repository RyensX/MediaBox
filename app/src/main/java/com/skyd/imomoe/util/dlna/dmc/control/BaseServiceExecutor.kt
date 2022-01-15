package com.skyd.imomoe.util.dlna.dmc.control

import android.os.Handler
import org.fourthline.cling.controlpoint.ControlPoint
import android.os.Looper
import com.skyd.imomoe.util.dlna.dmc.Utils
import org.fourthline.cling.controlpoint.ActionCallback
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IServiceActionCallback
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IAVServiceAction
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import com.skyd.imomoe.util.dlna.dmc.control.IServiceAction.IRendererServiceAction
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume
import org.fourthline.cling.support.renderingcontrol.callback.SetMute
import org.fourthline.cling.support.renderingcontrol.callback.GetMute
import com.skyd.imomoe.util.dlna.dmc.action.SetBrightness
import com.skyd.imomoe.util.dlna.dmc.action.GetBrightness
import com.skyd.imomoe.util.showToast
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.*
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo

internal abstract class BaseServiceExecutor protected constructor(
    private val mControlPoint: ControlPoint,
    protected val service: Service<*, *>?
) {
    private val mHandler = Handler(Looper.getMainLooper())

    protected fun invalidServiceAction(actionName: String): Boolean {
        return service?.getAction(actionName) == null
    }

    protected fun execute(actionCallback: ActionCallback) {
        mControlPoint.execute(actionCallback)
    }

    protected fun <T> notifySuccess(listener: IServiceActionCallback<T>?, t: T) {
        if (listener != null) notify { listener.onSuccess(t) }
    }

    protected fun notifyFailure(listener: IServiceActionCallback<*>?, errMsg: String?) {
        if (listener != null) notify { listener.onFailed(errMsg ?: "error") }
    }

    private fun notify(runnable: Runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(runnable)
        } else {
            runnable.run()
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // Implement
    // ---------------------------------------------------------------------------------------------------------
    internal class AVServiceExecutorImpl(controlPoint: ControlPoint, service: Service<*, *>?) :
        BaseServiceExecutor(controlPoint, service), IAVServiceAction {
        override fun cast(listener: IServiceActionCallback<String>?, uri: String, metadata: String) {
            if (invalidServiceAction("SetAVTransportURI")) return
            execute(object : SetAVTransportURI(service, uri, metadata) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, uri)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun play(listener: IServiceActionCallback<Void?>?) {
            if (invalidServiceAction("Play")) return
            execute(object : Play(service) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, null)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun pause(listener: IServiceActionCallback<Void?>?) {
            if (invalidServiceAction("Pause")) return
            execute(object : Pause(service) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, null)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun stop(listener: IServiceActionCallback<Void?>?) {
            if (invalidServiceAction("Stop")) return
            execute(object : Stop(service) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, null)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun seek(listener: IServiceActionCallback<Long>?, position: Long) {
            if (invalidServiceAction("Seek")) return
            execute(object : Seek(service, Utils.getStringTime(position)) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, position)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun getPositionInfo(listener: IServiceActionCallback<PositionInfo?>?) {
            if (invalidServiceAction("GetPositionInfo")) return
            execute(object : GetPositionInfo(service) {
                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    positionInfo: PositionInfo?
                ) {
                    notifySuccess(listener, positionInfo)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun getMediaInfo(listener: IServiceActionCallback<MediaInfo?>?) {
            if (invalidServiceAction("GetMediaInfo")) return
            execute(object : GetMediaInfo(service) {
                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    mediaInfo: MediaInfo?
                ) {
                    notifySuccess(listener, mediaInfo)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun getTransportInfo(listener: IServiceActionCallback<TransportInfo?>?) {
            if (invalidServiceAction("GetTransportInfo")) return
            execute(object : GetTransportInfo(service) {
                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    transportInfo: TransportInfo?
                ) {
                    notifySuccess(listener, transportInfo)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // Implement
    // ---------------------------------------------------------------------------------------------------------
    internal class RendererServiceExecutorImpl(
        controlPoint: ControlPoint,
        service: Service<*, *>?
    ) : BaseServiceExecutor(controlPoint, service), IRendererServiceAction {
        override fun setVolume(listener: IServiceActionCallback<Int>?, volume: Int) {
            if (invalidServiceAction("SetVolume")) return
            execute(object : SetVolume(service, volume.toLong()) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, volume)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun getVolume(listener: IServiceActionCallback<Int>?) {
            if (invalidServiceAction("GetVolume")) return
            execute(object : GetVolume(service) {
                override fun received(
                    actionInvocation: ActionInvocation<out Service<*, *>>?,
                    currentVolume: Int
                ) {
                    notifySuccess(listener, currentVolume)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun setMute(listener: IServiceActionCallback<Boolean>?, mute: Boolean) {
            if (invalidServiceAction("SetMute")) return
            execute(object : SetMute(service, mute) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    notifySuccess(listener, mute)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun isMute(listener: IServiceActionCallback<Boolean>?) {
            if (invalidServiceAction("GetMute")) return
            execute(object : GetMute(service) {
                override fun received(
                    actionInvocation: ActionInvocation<out Service<*, *>>?,
                    currentMute: Boolean
                ) {
                    notifySuccess(listener, currentMute)
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    notifyFailure(listener, defaultMsg)
                }
            })
        }

        override fun setBrightness(listener: IServiceActionCallback<Int>?, percent: Int) {
            if (invalidServiceAction("SetBrightness")) return
            try {
                execute(object : SetBrightness(service, percent.toLong()) {
                    override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                        notifySuccess(listener, percent)
                    }

                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) {
                        notifyFailure(listener, defaultMsg)
                    }
                })
            } catch (e: IllegalArgumentException) {
                // service is null: ActionInvocation -> Action can not be null
                e.printStackTrace()
                e.message?.showToast()
            }
        }

        override fun getBrightness(listener: IServiceActionCallback<Int>?) {
            if (invalidServiceAction("GetBrightness")) return
            try {
                execute(object : GetBrightness(service) {
                    override fun received(
                        actionInvocation: ActionInvocation<out Service<*, *>>?,
                        brightness: Int
                    ) {
                        notifySuccess(listener, brightness)
                    }

                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) {
                        notifyFailure(listener, defaultMsg)
                    }
                })
            } catch (e: IllegalArgumentException) {
                // service is null: ActionInvocation -> Action can not be null
                e.printStackTrace()
                e.message?.showToast()
            }
        }
    }
}