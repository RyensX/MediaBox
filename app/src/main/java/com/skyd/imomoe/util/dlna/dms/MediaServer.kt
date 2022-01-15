package com.skyd.imomoe.util.dlna.dms

import android.content.Context
import android.os.Build
import com.skyd.imomoe.util.dlna.Utils.getWiFiIPAddress
import com.skyd.imomoe.util.dlna.dms.IResourceServerFactory.DefaultResourceServerFactoryImpl
import org.fourthline.cling.model.ValidationException
import org.fourthline.cling.model.types.DeviceType
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder
import org.fourthline.cling.model.DefaultServiceManager
import org.fourthline.cling.model.types.UDN
import org.fourthline.cling.model.meta.*
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

open class MediaServer @JvmOverloads constructor(
    context: Context, factory: IResourceServerFactory = DefaultResourceServerFactoryImpl(PORT)
) {
    private val address by lazy { getWiFiIPAddress(context) }

    //TODO:remove local device field?
    val device: LocalDevice by lazy { createLocalDevice(context, address) }
    private var resourceServer: IResourceServer = factory.instance
    val inetAddress: String = String.format("%s:%s", address, factory.port)
    val baseUrl: String = String.format("http://%s:%s", address, factory.port)

    fun start() {
        resourceServer.startServer()
    }

    fun stop() {
        resourceServer.stopServer()
    }

    @Throws(ValidationException::class)
    @Suppress()
    protected open fun createLocalDevice(context: Context, ipAddress: String): LocalDevice {
        val identity = DeviceIdentity(createUniqueSystemIdentifier(ID_SALT, ipAddress))
        val type: DeviceType = UDADeviceType(TYPE_MEDIA_SERVER, VERSION)
        val details = DeviceDetails(
            String.format("DMS  (%s)", Build.MODEL),
            ManufacturerDetails(Build.MANUFACTURER),
            ModelDetails(Build.MODEL, DMS_DESC, "v1", baseUrl)
        )
        val service = AnnotationLocalServiceBinder().read(ContentDirectoryService::class.java)
        //TODO: ContentDirectoryService::class.java???
        service.manager = DefaultServiceManager(service, ContentDirectoryService().javaClass)
        var icon: Icon? = null
        try {
            icon = Icon(
                "image/png", 48, 48, 32, "msi.png",
                context.resources.assets.open("ic_launcher.png")
            )
        } catch (ignored: IOException) {
        }
        return LocalDevice(identity, type, details, icon, service)
    }

    companion object {
        private const val DMS_DESC = "MSI MediaServer"
        private const val ID_SALT = "GNaP-MediaServer"
        const val TYPE_MEDIA_SERVER = "MediaServer"
        private const val VERSION = 1
        private const val PORT = 8192
        private fun createUniqueSystemIdentifier(
            @Suppress("SameParameterValue") salt: String,
            ipAddress: String
        ): UDN {
            val builder = StringBuilder()
            builder.append(ipAddress)
            builder.append(Build.MODEL)
            builder.append(Build.MANUFACTURER)
            return try {
                val hash = MessageDigest.getInstance("MD5").digest(builder.toString().toByteArray())
                UDN(UUID(BigInteger(-1, hash).toLong(), salt.hashCode().toLong()))
            } catch (ex: Exception) {
                UDN(if (ex.message != null) ex.message else "UNKNOWN")
            }
        }
    }

    init {
        ContentFactory.initInstance(context, baseUrl)
//        resourceServer = factory.instance
    }
}