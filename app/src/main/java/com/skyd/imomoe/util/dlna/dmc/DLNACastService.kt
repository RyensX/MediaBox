package com.skyd.imomoe.util.dlna.dmc

import org.fourthline.cling.android.AndroidUpnpServiceImpl
import com.skyd.imomoe.util.dlna.dmc.ILogger.DefaultLoggerImpl
import org.fourthline.cling.android.FixedAndroidLogHandler
import android.content.Intent
import org.fourthline.cling.UpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration
import org.seamless.util.logging.LoggingUtil

/**
 *
 */
class DLNACastService : AndroidUpnpServiceImpl() {
    private val mLogger: ILogger = DefaultLoggerImpl(this)
    override fun onCreate() {
        mLogger.i(String.format("[%s] onCreate", javaClass.name))
        LoggingUtil.resetRootHandler(FixedAndroidLogHandler())
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mLogger.i(String.format("[%s] onStartCommand: %s , %s", javaClass.name, intent, flags))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mLogger.w(String.format("[%s] onDestroy", javaClass.name))
        super.onDestroy()
    }

    override fun createConfiguration(): UpnpServiceConfiguration {
        return DLNACastServiceConfiguration()
    }

    // ----------------------------------------------------------------
    // ---- configuration
    // ----------------------------------------------------------------
    private class DLNACastServiceConfiguration : AndroidUpnpServiceConfiguration() {
        override fun getRegistryMaintenanceIntervalMillis(): Int {
            return 5000 //default is 3000!
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