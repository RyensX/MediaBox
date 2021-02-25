
package com.skyd.imomoe.util.dlna.dms;

import android.content.Context;

import androidx.annotation.Nullable;
import com.skyd.imomoe.util.dlna.*;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public final class MediaServer {

    //TODO:remove local device field?
    private LocalDevice mDevice;
    private IResourceServer mResourceServer;
    private final String mInetAddress;
    private final String mBaseUrl;

    public MediaServer(Context context) {
        this(context, new IResourceServerFactory.DefaultResourceServerFactoryImpl(PORT));
    }

    public MediaServer(Context context, IResourceServerFactory factory) {
        String address = Utils.getWiFiIPAddress(context);
        mInetAddress = String.format("%s:%s", address, factory.getPort());
        mBaseUrl = String.format("http://%s:%s", address, factory.getPort());
        ContentFactory.initInstance(context, mBaseUrl);
        try {
            mDevice = createLocalDevice(context, address);
            mResourceServer = factory.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (mResourceServer != null) {
            mResourceServer.startServer();
        }
    }

    public void stop() {
        if (mResourceServer != null) {
            mResourceServer.stopServer();
        }
    }

    public String getInetAddress() {
        return mInetAddress;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    @Nullable
    public LocalDevice getDevice() {
        return mDevice;
    }

    private static final String DMS_DESC = "MSI MediaServer";
    private static final String ID_SALT = "GNaP-MediaServer";
    public final static String TYPE_MEDIA_SERVER = "MediaServer";
    private final static int VERSION = 1;
    private final static int PORT = 8192;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected LocalDevice createLocalDevice(Context context, String ipAddress) throws ValidationException {
        DeviceIdentity identity = new DeviceIdentity(createUniqueSystemIdentifier(ID_SALT, ipAddress));
        DeviceType type = new UDADeviceType(TYPE_MEDIA_SERVER, VERSION);
        DeviceDetails details = new DeviceDetails(String.format("DMS  (%s)", android.os.Build.MODEL),
                new ManufacturerDetails(android.os.Build.MANUFACTURER),
                new ModelDetails(android.os.Build.MODEL, DMS_DESC, "v1", mBaseUrl));
        final LocalService<?> service = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        service.setManager(new DefaultServiceManager(service, ContentDirectoryService.class));
        Icon icon = null;
        try {
            icon = new Icon("image/png", 48, 48, 32, "msi.png",
                    context.getResources().getAssets().open("ic_launcher.png"));
        } catch (IOException ignored) {
        }
        return new LocalDevice(identity, type, details, icon, service);
    }

    private static UDN createUniqueSystemIdentifier(@SuppressWarnings("SameParameterValue") String salt, String ipAddress) {
        StringBuilder builder = new StringBuilder();
        builder.append(ipAddress);
        builder.append(android.os.Build.MODEL);
        builder.append(android.os.Build.MANUFACTURER);
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(builder.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            return new UDN(ex.getMessage() != null ? ex.getMessage() : "UNKNOWN");
        }
    }
}
