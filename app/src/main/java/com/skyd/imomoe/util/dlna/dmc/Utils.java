package com.skyd.imomoe.util.dlna.dmc;

import android.content.ComponentName;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 *
 */
final public class Utils {

    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
    private static final String CAST_PARENT_ID = "1";
    private static final String CAST_CREATOR = "NLUpnpCast";
    private static final String CAST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(CAST_DATE_FORMAT, Locale.US);

    /**
     * 把时间戳转换成 00:00:00 格式
     *
     * @param timeMs 时间戳
     * @return 00:00:00 时间格式
     */
    public static String getStringTime(long timeMs) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.US);

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    /**
     * 把 00:00:00 格式转成时间戳
     *
     * @param formatTime 00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    public static long getIntTime(String formatTime) {
        if (!TextUtils.isEmpty(formatTime)) {
            String[] tmp = formatTime.split(":");

            if (tmp.length < 3) {
                return 0;
            }

            int second = Integer.valueOf(tmp[0]) * 3600 + Integer.valueOf(tmp[1]) * 60 + Integer.valueOf(tmp[2]);

            return second * 1000L;
        }

        return 0;
    }

    public static long parseTime(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public static String getMetadata(ICast cast) {
        if (cast instanceof ICast.ICastVideo) {
            ICast.ICastVideo castObj = (ICast.ICastVideo) cast;
            Res castRes = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), castObj.getSize(), cast.getUri());
            castRes.setBitrate(castObj.getBitrate());
            castRes.setDuration(getStringTime(castObj.getDurationMillSeconds()));
            String resolution = "description";
            VideoItem item = new VideoItem(cast.getId(), CAST_PARENT_ID, cast.getName(), CAST_CREATOR, castRes);
            item.setDescription(resolution);
            return createItemMetadata(item);
        }
        if (cast instanceof ICast.ICastAudio) {
            ICast.ICastAudio castObj = (ICast.ICastAudio) cast;
            Res castRes = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), castObj.getSize(), cast.getUri());
            castRes.setDuration(getStringTime(castObj.getDurationMillSeconds()));
            String resolution = "description";
            AudioItem item = new AudioItem(cast.getId(), CAST_PARENT_ID, cast.getName(), CAST_CREATOR, castRes);
            item.setDescription(resolution);
            return createItemMetadata(item);
        } else if (cast instanceof ICast.ICastImage) {
            ICast.ICastImage castObj = (ICast.ICastImage) cast;
            Res castRes = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), castObj.getSize(), cast.getUri());
            String resolution = "description";
            ImageItem item = new ImageItem(cast.getId(), CAST_PARENT_ID, cast.getName(), CAST_CREATOR, castRes);
            item.setDescription(resolution);
            return createItemMetadata(item);
        } else {
            return "";
        }
    }

    private static String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);
        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));
        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));
        metadata.append(String.format("<dc:date>%s</dc:date>", DATE_FORMAT.format(new Date())));

        // metadata.append(String.format("<upnp:album>%s</upnp:album>",item.get);
        // <res protocolInfo="http-get:*:audio/mpeg:*"
        // resolution="640x478">http://192.168.1.104:8088/Music/07.我醒著做夢.mp3</res>

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolInfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolInfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi.getAdditionalInfo());
            }

            // resolution, extra info, not adding yet
            String resolution = "";
            if (!TextUtils.isEmpty(res.getResolution())) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (!TextUtils.isEmpty(res.getDuration())) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            // metadata.append(String.format("<res %s>", protocolInfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolInfo, resolution, duration));

            // url
            metadata.append(res.getValue());

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }

    /**
     * @return device information like: [deviceType][name][manufacturer][udn]
     */
    public static String parseDeviceInfo(@NonNull RemoteDevice device) {
        return String.format("[%s][%s][%s][%s]",
                device.getType().getType(),
                device.getDetails().getFriendlyName(),
                device.getDetails().getManufacturerDetails().getManufacturer(),
                device.getIdentity().getUdn());
    }

    public static String parseDeviceService(@NonNull RemoteDevice device) {
        StringBuilder builder = new StringBuilder(device.getDetails().getFriendlyName());
        builder.append(":");
        for (RemoteService service : device.getServices()) {
            builder.append("\nservice:").append(service.getServiceType().getType());
            if (service.hasActions()) {
                builder.append("\nactions: ");
                List<Action<?>> list = Arrays.asList(service.getActions());
                Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                for (Action<?> action : list) {
                    builder.append(action.getName()).append(", ");
                }
            }
        }
        return builder.toString();
    }

    public static void logServiceConnected(ILogger mLogger, AndroidUpnpService upnpService, ComponentName componentName, IBinder iBinder) {
        mLogger.i("---------------------------------------------------------------------------");
        mLogger.i("---------------------------------------------------------------------------");
        mLogger.i(String.format("[%s] connected %s", componentName.getShortClassName(), iBinder.getClass().getName()));
        mLogger.i(String.format("[UpnpService]: %s@0x%s", upnpService.get().getClass().getName(), toHexString(upnpService.get().hashCode())));
        mLogger.i(String.format("[Registry]: listener=%s, devices=%s", upnpService.getRegistry().getListeners().size(), upnpService.getRegistry().getDevices().size()));
        mLogger.i("---------------------------------------------------------------------------");
        mLogger.i("---------------------------------------------------------------------------");
    }

    private static String toHexString(int hashCode) {
        return Integer.toHexString(hashCode).toUpperCase(Locale.US);
    }
}
