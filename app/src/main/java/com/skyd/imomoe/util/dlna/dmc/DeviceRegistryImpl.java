package com.skyd.imomoe.util.dlna.dmc;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.Collection;

/**
 *
 */
final class DeviceRegistryImpl extends DefaultRegistryListener {

    private final OnDeviceRegistryListener mOnDeviceRegistryListener;
    private final ILogger mLogger = new ILogger.DefaultLoggerImpl(this);
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean mIgnoreUpdate = true;

    public DeviceRegistryImpl(@NonNull OnDeviceRegistryListener listener) {
        mOnDeviceRegistryListener = listener;
        setIgnoreUpdateEvent(true);
    }

    public void setIgnoreUpdateEvent(boolean ignoreUpdate) {
        mIgnoreUpdate = ignoreUpdate;
    }

    public void setDevices(@SuppressWarnings("rawtypes") Collection<Device> collection) {
        if (collection != null && collection.size() > 0) {
            for (Device<?, ?, ?> device : collection) {
                notifyDeviceAdd(device);
            }
        }
    }

    // Discovery performance optimization for very slow Android devices!
    // This function will called early than 'remoteDeviceAdded',but the device services maybe not entirely.
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        mLogger.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        mLogger.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        mLogger.i(String.format("[%s] discovery started...", device.getDetails().getFriendlyName()));
    }

    //End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz)
    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        mLogger.e("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        mLogger.e("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        mLogger.e(String.format("[%s] discovery failed...", device.getDetails().getFriendlyName()));
        mLogger.e(ex.toString());
    }

    // remote device
    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        mLogger.i("remoteDeviceAdded: " + Utils.parseDeviceInfo(device));
        mLogger.i(Utils.parseDeviceService(device));
        notifyDeviceAdd(device);
    }

    @Override
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
        if (!mIgnoreUpdate) {
            mLogger.d("remoteDeviceUpdated: " + Utils.parseDeviceInfo(device));
            notifyDeviceUpdate(device);
        }
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        mLogger.w("remoteDeviceRemoved: " + Utils.parseDeviceInfo(device));
        notifyDeviceRemove(device);
    }

    // local device
    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        super.localDeviceAdded(registry, device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        super.localDeviceRemoved(registry, device);
    }

    private void notifyDeviceAdd(final Device<?, ?, ?> device) {
        mHandler.post(() -> mOnDeviceRegistryListener.onDeviceAdded(device));
    }

    private void notifyDeviceUpdate(final Device<?, ?, ?> device) {
        mHandler.post(() -> mOnDeviceRegistryListener.onDeviceUpdated(device));
    }

    private void notifyDeviceRemove(final Device<?, ?, ?> device) {
        mHandler.post(() -> mOnDeviceRegistryListener.onDeviceRemoved(device));
    }
}
