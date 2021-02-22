package com.skyd.imomoe.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import java.net.URL


object GlideUtil {
    fun getGlideUrl(url: String, referer: String = "http://www.yhdm.io/") = GlideUrl(
        url,
        LazyHeaders.Builder()
            .addHeader("Referer", referer)
            .addHeader("Host", URL(url).host)
            .addHeader("Accept", "*/*")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Connection", "keep-alive")
            .addHeader("User-Agent", Const.Request.USER_AGENT)
            .build()
    )

    fun ImageView.loadImage(
        context: Context,
        url: String,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_main_color_3_24
    ) {
        val glideUrl = getGlideUrl(url, referer ?: "http://www.yhdm.io/")
        Glide.with(context).load(glideUrl)
            .placeholder(placeholder)
            .error(error)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
//            .transition(withCrossFade())
            .into(this)
    }
}