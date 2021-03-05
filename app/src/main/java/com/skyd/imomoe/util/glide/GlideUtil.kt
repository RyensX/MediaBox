package com.skyd.imomoe.util.glide

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.SimpleTarget
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import java.net.URL
import java.net.URLEncoder
import kotlin.random.Random


object GlideUtil {
    fun getGlideUrl(url: String, referer: String = "http://www.yhdm.io/") = GlideUrl(
        url,
        LazyHeaders.Builder()
            .addHeader("Referer", referer)
            .addHeader("Host", URL(url).host)
            .addHeader("Accept", "*/*")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Connection", "keep-alive")
            .addHeader(
                "User-Agent",
                Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
            )
            .build()
    )

    fun ImageView.loadImage(
        activity: Activity,
        url: String,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_main_color_3_24
    ) {
        if (!activity.isDestroyed) loadImage(activity as Context, url, referer, placeholder, error)
    }

    fun ImageView.loadImage(
        context: Context,
        url: String,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_main_color_3_24
    ) {
        var amendReferer = referer
        if (amendReferer?.startsWith("http://www.yhdm.io") == false)
            amendReferer = "http://www.yhdm.io/"
        if (referer == "http://www.yhdm.io" || referer == "http://www.yhdm.io.") amendReferer += "/"
        val glideUrl = getGlideUrl(url, amendReferer ?: "http://www.yhdm.io/")
        //使用了自定义的OkHttp，OkHttpGlideModule，因此是GlideApp
        GlideApp.with(context).load(glideUrl)
            .placeholder(placeholder)
            .error(error)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false)
//            .dontAnimate()
            .transition(withCrossFade())
            .into(this)

    }
}