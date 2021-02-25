package com.skyd.imomoe.util.dlna.dmc.control;

import com.skyd.imomoe.util.dlna.dmc.DLNACastManager;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;

/**
 *
 */
interface IServiceFactory {
    IServiceAction.IAVServiceAction getAvService();

    IServiceAction.IRendererServiceAction getRenderService();

    // ------------------------------------------------------------------------------------------
    // Implement
    // ------------------------------------------------------------------------------------------
    final class ServiceFactoryImpl implements IServiceFactory {
        private final IServiceAction.IAVServiceAction mAvAction;
        private final IServiceAction.IRendererServiceAction mRenderAction;

        public ServiceFactoryImpl(ControlPoint controlPoint, Device<?, ?, ?> device) {
            Service<?, ?> avService = device.findService(DLNACastManager.SERVICE_AV_TRANSPORT);
            mAvAction = new BaseServiceExecutor.AVServiceExecutorImpl(controlPoint, avService);
            Service<?, ?> rendererService = device.findService(DLNACastManager.SERVICE_RENDERING_CONTROL);
            mRenderAction = new BaseServiceExecutor.RendererServiceExecutorImpl(controlPoint, rendererService);
        }

        @Override
        public IServiceAction.IAVServiceAction getAvService() {
            return mAvAction;
        }

        @Override
        public IServiceAction.IRendererServiceAction getRenderService() {
            return mRenderAction;
        }
    }
}
