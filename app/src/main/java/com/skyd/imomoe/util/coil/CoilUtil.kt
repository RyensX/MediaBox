package com.skyd.imomoe.util.coil

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.Coil
import coil.ImageLoader
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.util.CoilUtils
import coil.util.DebugLogger
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api.Companion.MAIN_URL
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.net.DoH
import com.skyd.imomoe.util.Util.showToastOnIOThread
import com.skyd.imomoe.util.debug
import okhttp3.OkHttpClient
import java.net.URL
import kotlin.random.Random


object CoilUtil {
    init {
        ImageLoader.Builder(App.context)
            .crossfade(400)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(App.context))
                    .addInterceptor(DoH.doHInterceptor)
                    .build()
            }
            .apply { debug { logger(DebugLogger()) } }
            .build().apply {
                Coil.setImageLoader(this)
            }
    }

    fun ImageView.loadImage(
        url: String,
        builder: ImageRequest.Builder.() -> Unit = {},
    ) {
        if (url.isEmpty()) {
            "cover image url must not be null or empty".showToastOnIOThread()
            return
        }

        this.load(url, builder = builder)
    }

    fun ImageView.loadImage(
        url: String,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_main_color_3_24_skin
    ) {
        var amendReferer = referer
        if (amendReferer?.startsWith(MAIN_URL) == false)
            amendReferer = MAIN_URL//"http://www.yhdm.io/"
        if (referer == MAIN_URL || referer == MAIN_URL) amendReferer += "/"

        loadImage(url) {
            placeholder(placeholder)
            error(error)
            addHeader("Referer", amendReferer ?: MAIN_URL)
            addHeader("Host", URL(url).host)
            addHeader("Accept", "*/*")
            addHeader("Accept-Encoding", "gzip, deflate")
            addHeader("Connection", "keep-alive")
            addHeader(
                "User-Agent",
                Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
            )
        }
    }


    fun clearMemoryDiskCache() {
        App.context.imageLoader.memoryCache.clear()
        CoilUtils.createDefaultCache(App.context).delete()
    }
}