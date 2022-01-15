package com.skyd.imomoe.util.dlna.dms

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.math.min

internal class NanoHttpServer(port: Int) : NanoHTTPD(port), IResourceServer {
    companion object {
        private val MIME_TYPE: MutableMap<String, String> = HashMap()
        private const val MIME_PLAINTEXT = "text/plain"

        init {
            MIME_TYPE["jpg"] = "image/*"
            MIME_TYPE["jpeg"] = "image/*"
            MIME_TYPE["png"] = "image/*"
            MIME_TYPE["mp3"] = "audio/*"
            MIME_TYPE["mp4"] = "video/*"
            MIME_TYPE["wav"] = "video/*"
        }
    }

    override fun serve(session: IHTTPSession): Response {
        println("uri: " + session.uri)
        println("header: " + session.headers.toString())
        println("params: " + session.parms.toString())
        val uri = session.uri
        if (uri.isNullOrEmpty() || !uri.startsWith("/")) {
            return newChunkedResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, null)
        }
        val file = File(uri)
        if (!file.exists() || file.isDirectory) {
            return newChunkedResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, null)
        }
        val type = uri.substring(min(uri.length, uri.lastIndexOf(".") + 1))
            .toLowerCase(Locale.US)
        var mimeType = MIME_TYPE[type]
        if (mimeType.isNullOrEmpty()) {
            mimeType = MIME_PLAINTEXT
        }
        return try {
            newChunkedResponse(Response.Status.OK, mimeType, FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            newChunkedResponse(Response.Status.SERVICE_UNAVAILABLE, mimeType, null)
        }
    }

    override fun startServer() {
        try {
            start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun stopServer() {
        stop()
    }
}