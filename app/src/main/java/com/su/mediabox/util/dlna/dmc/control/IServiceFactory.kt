package com.su.mediabox.util.dlna.dmc.control

import com.su.mediabox.util.dlna.dmc.control.IServiceAction.IAVServiceAction
import com.su.mediabox.util.dlna.dmc.control.IServiceAction.IRendererServiceAction
import org.fourthline.cling.controlpoint.ControlPoint
import com.su.mediabox.util.dlna.dmc.DLNACastManager
import com.su.mediabox.util.dlna.dmc.control.BaseServiceExecutor.AVServiceExecutorImpl
import com.su.mediabox.util.dlna.dmc.control.BaseServiceExecutor.RendererServiceExecutorImpl
import org.fourthline.cling.model.meta.Device

internal interface IServiceFactory {
    val avService: IAVServiceAction
    val renderService: IRendererServiceAction

    // ------------------------------------------------------------------------------------------
    // Implement
    // ------------------------------------------------------------------------------------------
    class ServiceFactoryImpl(controlPoint: ControlPoint, device: Device<*, *, *>) :
        IServiceFactory {
        private val mAvAction: IAVServiceAction
        private val mRenderAction: IRendererServiceAction

        init {
            val avService = device.findService(DLNACastManager.SERVICE_AV_TRANSPORT)
            mAvAction = AVServiceExecutorImpl(controlPoint, avService)
            val rendererService = device.findService(DLNACastManager.SERVICE_RENDERING_CONTROL)
            mRenderAction = RendererServiceExecutorImpl(controlPoint, rendererService)
        }

        override val avService: IAVServiceAction
            get() = mAvAction
        override val renderService: IRendererServiceAction
            get() = mRenderAction
    }
}