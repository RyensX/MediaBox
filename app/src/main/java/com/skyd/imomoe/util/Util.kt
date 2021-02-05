package com.skyd.imomoe.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.view.activity.AnimeDetailActivity
import com.skyd.imomoe.view.activity.PlayActivity
import java.net.URLDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern


object Util {
    fun getAppVersionCode(): Long {
        var appVersionCode: Long = 0
        try {
            val packageInfo = App.context.applicationContext
                .packageManager
                .getPackageInfo(App.context.packageName, 0)
            appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersionCode
    }

    fun getAppVersionName(): String {
        var appVersionName = ""
        try {
            val packageInfo = App.context.applicationContext
                .packageManager
                .getPackageInfo(App.context.packageName, 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersionName
    }

    fun EditText.showKeyboard() {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val inputManager =
            App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }

    fun String.getSubString(s: String, e: String): List<String> {
        val regex = s + "(.*?)" + e
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(this)
        val list: MutableList<String> = ArrayList()
        while (m.find()) {
            list.add(m.group(1))
        }
        return list
    }

    fun dp2px(dpValue: Float): Int {
        val scale = App.context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun setColorStatusBar(
        window: Window,
        statusBarColor: Int,
        darkTextColor: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = statusBarColor //设置状态栏颜色
            if (darkTextColor) window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun getStatusBarHeight(): Int {
        var height = 0
        val resourceId: Int =
            App.context.resources
                .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return App.context.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(App.context, this, duration).show()
    }

    fun CharSequence.showToastOnThread(duration: Int = Toast.LENGTH_SHORT) {
        Looper.prepare()
        Toast.makeText(App.context, this, duration).show()
        Looper.loop()
    }

    fun getScreenHeight(includeVirtualKey: Boolean): Int {
        val display =
            (App.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val outPoint = Point()
        // 可能有虚拟按键的情况
        if (includeVirtualKey) display.getRealSize(outPoint)
        else display.getSize(outPoint)
        return outPoint.y
    }

    fun getScreenWidth(includeVirtualKey: Boolean): Int {
        val display =
            (App.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val outPoint = Point()
        // 可能有虚拟按键的情况
        if (includeVirtualKey) display.getRealSize(outPoint)
        else display.getSize(outPoint)
        return outPoint.x
    }

    fun ImageView.loadImage(
        url: String,
        round: Int = 0,
        options: RequestOptions.() -> RequestOptions
    ) {
        if (round == 0) {
            Glide.with(App.context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .apply(RequestOptions().options()).into(this)
        } else {
            Glide.with(App.context).load(url).apply(
                RequestOptions.bitmapTransform(
                    RoundedCorners(round)
                )
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false)
            ).apply(RequestOptions().options()).into(this)
        }
    }

    fun ImageView.loadImage(url: String, round: Int = 0) {
        if (round == 0) {
            Glide.with(App.context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(this)
        } else {
            Glide.with(App.context).load(url).apply(
                RequestOptions.bitmapTransform(RoundedCorners(round))
            ).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false).into(this)
        }
    }

    fun process(fragment: Fragment, actionUrl: String?, toastTitle: String = "") {
        val activity = fragment.activity
        if (activity != null)
            process(activity, actionUrl, toastTitle)
    }

    fun process(activity: Activity, actionUrl: String?, toastTitle: String = "") {
        if (actionUrl == null) return
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        when {
            decodeUrl.startsWith(Const.ActionUrl.ANIME_DETAIL) -> {     //番剧封面点击进入
                activity.startActivity(
                    Intent(activity, AnimeDetailActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_PLAY) -> {     //番剧每一集点击进入
                val playCode = actionUrl.getSubString("\\/v\\/", "\\.")[0].split("-")
                if (playCode.size >= 2) {
                    activity.startActivity(
                        Intent(activity, PlayActivity::class.java)
                            .putExtra("partUrl", actionUrl)
                    )
                } else {
                    App.context.getString(R.string.error_play_episode).showToast()
                }
            }
            decodeUrl.length == 8 && decodeUrl.startsWith("/") && decodeUrl.endsWith("/")
                    && decodeUrl.replace("/", "").length == 6 -> {     //如201907月新番列表
                "${toastTitle},${App.context.resources.getString(R.string.currently_not_supported)}".showToast()
            }
            else -> {
                "${toastTitle},${App.context.resources.getString(R.string.currently_not_supported)}".showToast()
            }
        }
    }

}