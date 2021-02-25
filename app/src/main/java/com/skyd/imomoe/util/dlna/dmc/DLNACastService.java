package com.skyd.imomoe.util.dlna.dmc;

import android.content.Intent;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;

/**
 *
 */
public class DLNACastService extends AndroidUpnpServiceImpl {
    private final ILogger mLogger = new ILogger.DefaultLoggerImpl(this);

    @Override
    public void onCreate() {
        mLogger.i(String.format("[%s] onCreate", getClass().getName()));
        org.seamless.util.logging.LoggingUtil.resetRootHandler(new FixedAndroidLogHandler());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogger.i(String.format("[%s] onStartCommand: %s , %s", getClass().getName(), intent, flags));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLogger.w(String.format("[%s] onDestroy", getClass().getName()));
        super.onDestroy();
    }

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new DLNACastServiceConfiguration();
    }

    // ----------------------------------------------------------------
    // ---- configuration
    // ----------------------------------------------------------------
    private static final class DLNACastServiceConfiguration extends AndroidUpnpServiceConfiguration {

        @Override
        public int getRegistryMaintenanceIntervalMillis() {
            return 5000; //default is 3000!
        }

        // @Override
        // public ServiceType[] getExclusiveServiceTypes() {
        //     return new ServiceType[]{
        //             DLNACastManager.SERVICE_RENDERING_CONTROL,
        //             DLNACastManager.SERVICE_AV_TRANSPORT,
        //             DLNACastManager.SERVICE_CONNECTION_MANAGER};
        // }
    }
}
