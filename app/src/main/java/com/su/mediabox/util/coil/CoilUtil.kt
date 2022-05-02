package com.su.mediabox.util.coil

import android.content.Context
import android.util.Base64
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.*
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.ImageRequest
import coil.request.Options
import coil.util.DebugLogger
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.config.Api
import com.su.mediabox.config.Api.Companion.MAIN_URL
import com.su.mediabox.net.okhttpClient
import com.su.mediabox.util.debug
import com.su.mediabox.util.logE
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.util.getOrInit
import okhttp3.OkHttpClient
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.net.URL

object CoilUtil {
    private val imageLoaderBuilder = ImageLoader.Builder(App.context)
        .crossfade(400)
        .apply {
            debug { logger(DebugLogger()) }
        }

    init {
        setOkHttpClient(okhttpClient)
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient) {
        imageLoaderBuilder.okHttpClient(
            okHttpClient.newBuilder().build()
        ).build().apply { Coil.setImageLoader(this) }
    }

    fun ImageView.loadImage(
        url: String,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_main_color_3_24_skin,
        builder: ImageRequest.Builder.() -> Unit = {}
    ) = runCatching {
        // 是本地drawable
        url.toIntOrNull()?.let { drawableResId ->
            load(drawableResId) {
                builder()
                placeholder(placeholder)
                error(error)
            }
            return@runCatching
        }

        if (url.isEmpty()) {
            logE("loadImage", "cover image url must not be null or empty")
            return@runCatching
        }

        // 是网络图片

        if (tag == url)
            return@runCatching

        var amendReferer = referer ?: MAIN_URL
        if (!amendReferer.startsWith(MAIN_URL))
            amendReferer = MAIN_URL//"http://www.yhdm.io/"
        if (referer == MAIN_URL || referer == MAIN_URL) amendReferer += "/"

        load(url) {
            builder()
            placeholder(placeholder)
            error(error)
            addHeader("Referer", referer ?: Api.refererProcessor?.processor(url) ?: "")
            addHeader("Host", URL(url).host)
            addHeader("Accept", "*/*")
            addHeader("Accept-Encoding", "gzip, deflate")
            addHeader("Connection", "keep-alive")
            addHeader("User-Agent", Constant.Request.getRandomUserAgent())
            listener { _, _ ->
                tag = url
            }
        }
    }.onFailure {
        if (url.isNotBlank())
            load(url)
    }

    fun ImageView.loadGaussianBlurCover(
        url: String,
        context: Context,
        radius: Float = 25F,
        sampling: Float = 2F,
        dark: Float = 0.7F
    ) = loadImage(url) {
        transformations(DarkBlurTransformation(context, radius, sampling, dark))
    }

    object Base64FetcherFactory : Fetcher.Factory<Base64FetcherFactory.Base64Image> {

        private val base64ImagePool = mutableMapOf<Int, Base64Image>()

        fun obtainBase64Image(base64: String) =
            base64ImagePool.getOrInit(base64.hashCode()) {
                Base64Image(base64)
            }

        class Base64Image internal constructor(val base64: String)

        override fun create(
            data: Base64Image,
            options: Options,
            imageLoader: ImageLoader
        ) =
            Fetcher {
                val imageByteArray = Base64.decode(data.base64.split(",")[1], Base64.DEFAULT)
                SourceResult(
                    source = ImageSource(
                        ByteArrayInputStream(imageByteArray).source().buffer(),
                        App.context
                    ),
                    mimeType = null,
                    dataSource = DataSource.MEMORY
                )
            }
    }

    fun clearMemoryDiskCache() {
        App.context.imageLoader.memoryCache?.clear()
    }
}