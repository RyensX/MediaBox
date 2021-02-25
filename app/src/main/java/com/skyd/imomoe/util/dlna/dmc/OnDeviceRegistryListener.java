package com.skyd.imomoe.util.dlna.dmc;

import org.fourthline.cling.model.meta.Device;

/**
 * this listener call in UI thread.
 */
public interface OnDeviceRegistryListener {
    void onDeviceAdded(Device<?, ?, ?> device);

    void onDeviceUpdated(Device<?, ?, ?> device);

    void onDeviceRemoved(Device<?, ?, ?> device);
}
