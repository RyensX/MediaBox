package com.skyd.imomoe.util.dlna.dmc.control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.util.dlna.dmc.ICast;
import com.skyd.imomoe.util.dlna.dmc.Utils;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

import java.util.Map;

public class ControlImpl implements ICastInterface.IControl {

    private final IServiceFactory mServiceFactory;
    private final Device<?, ?, ?> mDevice;
    private final Map<String, IServiceAction.IServiceActionCallback<?>> mCallbackMap;

    public ControlImpl(@NonNull ControlPoint controlPoint, @NonNull Device<?, ?, ?> device, Map<String, IServiceAction.IServiceActionCallback<?>> map) {
        mDevice = device;
        mCallbackMap = map;
        mServiceFactory = new IServiceFactory.ServiceFactoryImpl(controlPoint, device);
    }

    @Override
    public void cast(Device<?, ?, ?> device, ICast object) {
        mServiceFactory.getAvService().cast(new ICastInterface.CastEventListener() {
            @Override
            public void onSuccess(String result) {
                IServiceAction.IServiceActionCallback<Object> listener = getCallback(IServiceAction.ServiceAction.CAST);
                if (listener != null) listener.onSuccess(result);
            }

            @Override
            public void onFailed(String errMsg) {
                IServiceAction.IServiceActionCallback<Object> listener = getCallback(IServiceAction.ServiceAction.CAST);
                if (listener != null) listener.onFailed(errMsg);
            }
        }, object.getUri(), Utils.getMetadata(object));
    }

    @Override
    public boolean isCasting(Device<?, ?, ?> device) {
        return mDevice != null && mDevice.equals(device);
    }

    @Override
    public void stop() {
        mServiceFactory.getAvService().stop(getCallback(IServiceAction.ServiceAction.STOP));
    }

    @Override
    public void play() {
        mServiceFactory.getAvService().play(getCallback(IServiceAction.ServiceAction.PLAY));
    }

    @Override
    public void pause() {
        mServiceFactory.getAvService().pause(getCallback(IServiceAction.ServiceAction.PAUSE));
    }

    @Override
    public void seekTo(long position) {
        mServiceFactory.getAvService().seek(getCallback(IServiceAction.ServiceAction.SEEK_TO), position);
    }

    @Override
    public void setVolume(int percent) {
        mServiceFactory.getRenderService().setVolume(getCallback(IServiceAction.ServiceAction.SET_VOLUME), percent);
    }

    @Override
    public void setMute(boolean mute) {
        mServiceFactory.getRenderService().setMute(getCallback(IServiceAction.ServiceAction.SET_MUTE), mute);
    }

    @Override
    public void setBrightness(int percent) {
        mServiceFactory.getRenderService().setBrightness(getCallback(IServiceAction.ServiceAction.SET_BRIGHTNESS), percent);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> IServiceAction.IServiceActionCallback<T> getCallback(IServiceAction.ServiceAction action) {
        Object result = mCallbackMap.get(action.name());
        if (result == null) return null;
        return (IServiceAction.IServiceActionCallback<T>) result;
    }
}
