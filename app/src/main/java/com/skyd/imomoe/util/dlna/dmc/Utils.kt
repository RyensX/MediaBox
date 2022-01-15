package com.skyd.imomoe.util.dlna.dmc

import com.skyd.imomoe.util.dlna.dmc.ICast.ICastVideo
import org.fourthline.cling.support.model.ProtocolInfo
import org.fourthline.cling.support.model.item.VideoItem
import com.skyd.imomoe.util.dlna.dmc.ICast.ICastAudio
import org.fourthline.cling.support.model.item.AudioItem
import com.skyd.imomoe.util.dlna.dmc.ICast.ICastImage
import org.fourthline.cling.support.model.item.ImageItem
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.android.AndroidUpnpService
import android.content.ComponentName
import android.os.IBinder
import okhttp3.internal.toHexString
import org.fourthline.cling.model.meta.Action
import org.fourthline.cling.support.model.Res
import org.seamless.util.MimeType
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    private const val DIDL_LITE_FOOTER = "</DIDL-Lite>"
    private const val DIDL_LITE_HEADER =
        "<?xml version=\"1.0\"?>" + "<DIDL-Lite " +
                "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
                "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
                "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">"
    private const val CAST_PARENT_ID = "1"
    private const val CAST_CREATOR = "NLUpnpCast"
    private const val CAST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    private val DATE_FORMAT = SimpleDateFormat(CAST_DATE_FORMAT, Locale.US)

    /**
     * 把时间戳转换成 00:00:00 格式
     *
     * @param timeMs 时间戳
     * @return 00:00:00 时间格式
     */
    fun getStringTime(timeMs: Long): String {
        val formatBuilder = StringBuilder()
        val formatter = Formatter(formatBuilder, Locale.US)
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    /**
     * 把 00:00:00 格式转成时间戳
     *
     * @param formatTime 00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    fun getIntTime(formatTime: String): Long {
        if (formatTime.isNotEmpty()) {
            val tmp = formatTime.split(":").toTypedArray()
            if (tmp.size < 3) return 0
            val second = tmp[0].toInt() * 3600 + tmp[1].toInt() * 60 + tmp[2].toInt()
            return second * 1000L
        }
        return 0
    }

    fun parseTime(s: String): Long {
        return try {
            s.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun getMetadata(cast: ICast): String {
        return when (cast) {
            is ICastVideo -> {
                val castRes =
                    Res(MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), cast.size, cast.uri)
                castRes.bitrate = cast.bitrate
                castRes.duration = getStringTime(cast.durationMillSeconds)
                val resolution = "description"
                val item = VideoItem(cast.id, CAST_PARENT_ID, cast.name, CAST_CREATOR, castRes)
                item.description = resolution
                createItemMetadata(item)
            }
            is ICastAudio -> {
                val castRes = Res(
                    MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), cast.size, cast.uri
                )
                castRes.duration = getStringTime(cast.durationMillSeconds)
                val resolution = "description"
                val item = AudioItem(cast.id, CAST_PARENT_ID, cast.name, CAST_CREATOR, castRes)
                item.description = resolution
                createItemMetadata(item)
            }
            is ICastImage -> {
                val castRes = Res(
                    MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), cast.size, cast.uri
                )
                val resolution = "description"
                val item = ImageItem(cast.id, CAST_PARENT_ID, cast.name, CAST_CREATOR, castRes)
                item.description = resolution
                createItemMetadata(item)
            }
            else -> ""
        }
    }

    private fun createItemMetadata(item: DIDLObject): String {
        val metadata = StringBuilder()
        metadata.append(DIDL_LITE_HEADER)
        metadata.append(
            String.format(
                "<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">",
                item.id,
                item.parentID,
                if (item.isRestricted) "1" else "0"
            )
        )
        metadata.append(String.format("<dc:title>%s</dc:title>", item.title))
        var creator = item.creator
        if (creator != null) {
            creator = creator.replace("<".toRegex(), "_")
            creator = creator.replace(">".toRegex(), "_")
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator))
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.clazz.value))
        metadata.append(String.format("<dc:date>%s</dc:date>", DATE_FORMAT.format(Date())))

        // metadata.append(String.format("<upnp:album>%s</upnp:album>",item.get);
        // <res protocolInfo="http-get:*:audio/mpeg:*"
        // resolution="640x478">http://192.168.1.104:8088/Music/07.我醒著做夢.mp3</res>
        val res = item.firstResource
        if (res != null) {
            // protocol info
            var protocolInfo = ""
            val pi = res.protocolInfo
            if (pi != null) {
                protocolInfo = String.format(
                    "protocolInfo=\"%s:%s:%s:%s\"",
                    pi.protocol,
                    pi.network,
                    pi.contentFormatMimeType,
                    pi.additionalInfo
                )
            }

            // resolution, extra info, not adding yet
            var resolution = ""
            if (!res.resolution.isNullOrEmpty()) {
                resolution = String.format("resolution=\"%s\"", res.resolution)
            }

            // duration
            var duration = ""
            if (!res.duration.isNullOrEmpty()) {
                duration = String.format("duration=\"%s\"", res.duration)
            }

            // res begin
            // metadata.append(String.format("<res %s>", protocolInfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolInfo, resolution, duration))

            // url
            metadata.append(res.value)

            // res end
            metadata.append("</res>")
        }
        metadata.append("</item>")
        metadata.append(DIDL_LITE_FOOTER)
        return metadata.toString()
    }

    /**
     * @return device information like: [deviceType][name][manufacturer][udn]
     */
    fun parseDeviceInfo(device: RemoteDevice): String {
        return String.format(
            "[%s][%s][%s][%s]",
            device.type.type,
            device.details.friendlyName,
            device.details.manufacturerDetails.manufacturer,
            device.identity.udn
        )
    }

    fun parseDeviceService(device: RemoteDevice): String {
        val builder = StringBuilder(device.details.friendlyName)
        builder.append(":")
        for (service in device.services) {
            builder.append("\nservice:").append(service.serviceType.type)
            if (service.hasActions()) {
                builder.append("\nactions: ")
                val list: MutableList<Action<*>> = mutableListOf(*service.actions)
                list.sortWith { o1: Action<*>, o2: Action<*> -> o1.name.compareTo(o2.name) }
                for (action in list) {
                    builder.append(action.name).append(", ")
                }
            }
        }
        return builder.toString()
    }

    fun logServiceConnected(
        mLogger: ILogger,
        upnpService: AndroidUpnpService,
        componentName: ComponentName,
        iBinder: IBinder
    ) {
        mLogger.i("---------------------------------------------------------------------------")
        mLogger.i("---------------------------------------------------------------------------")
        mLogger.i(
            String.format(
                "[%s] connected %s",
                componentName.shortClassName,
                iBinder.javaClass.name
            )
        )
        mLogger.i(
            String.format(
                "[UpnpService]: %s@0x%s",
                upnpService.get().javaClass.name,
                toHexString(upnpService.get().hashCode())
            )
        )
        mLogger.i(
            String.format(
                "[Registry]: listener=%s, devices=%s",
                upnpService.registry.listeners.size,
                upnpService.registry.devices.size
            )
        )
        mLogger.i("---------------------------------------------------------------------------")
        mLogger.i("---------------------------------------------------------------------------")
    }

    private fun toHexString(hashCode: Int): String {
        return hashCode.toHexString().toUpperCase(Locale.US)
    }
}