package com.skyd.imomoe.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.view.widget.AnimeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


object Util {
    fun openBrowser(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        App.context.startActivity(intent)
    }

    /**
     * 获取重定向最终的地址
     * @param path
     */
    fun getRedirectUrl(path: String): String {
        var url = path
        return try {
            var conn: HttpURLConnection
            do {
                conn = URL(url).openConnection() as HttpURLConnection
                conn.headerFields
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 5000
                conn.getHeaderField("Location")?.let {
                    url = conn.getHeaderField("Location")
                }
                conn.disconnect()
            } while (conn.responseCode == 302 && conn.getHeaderField("Location") != null)
            url
        } catch (e: IOException) {
            e.printStackTrace()
            url
        }
    }


    /**
     * 计算距今时间
     * @param timeStamp 过去的时间戳
     */
    fun time2Now(timeStamp: Long): String {
        val nowTimeStamp = System.currentTimeMillis()
        var result = "非法输入"
        val dateDiff = nowTimeStamp - timeStamp
        if (dateDiff >= 0) {
            val bef = Calendar.getInstance().apply { time = Date(timeStamp) }
            val aft = Calendar.getInstance().apply { time = Date(nowTimeStamp) }
            val second = dateDiff / 1000.0
            val minute = second / 60.0
            val hour = minute / 60.0
            val day = hour / 24.0
            val month =
                aft[Calendar.MONTH] - bef[Calendar.MONTH] + (aft[Calendar.YEAR] - bef[Calendar.YEAR]) * 12
            val year = month / 12.0
            result = when {
                year.toInt() > 0 -> "${year.toInt()}年前"
                month > 0 -> "${month}个月前"
                day.toInt() > 0 -> "${day.toInt()}天前"
                hour.toInt() > 0 -> "${hour.toInt()}小时前"
                minute.toInt() > 0 -> "${minute.toInt()}分钟前"
                else -> "刚刚"
            }
        }
        return result
    }

    fun copyText2Clipboard(context: Context, text: String) {
        val systemService: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        systemService.setPrimaryClip(ClipData.newPlainText("text", text))
    }

    fun isNewVersion(version: String): Boolean {
        val currentVersion = getAppVersionName()
        return version.replace(".", "")
            .replace("v", "")
            .replace("V", "")
            .replace(" ", "").toInt() >
                currentVersion.replace(".", "").toInt()
    }

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

    fun getAppName(): String? {
        return try {
            val packageManager = App.context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                App.context.packageName, 0
            )
            val labelRes: Int = packageInfo.applicationInfo.labelRes
            App.context.resources.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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

    fun setTransparentStatusBar(
        window: Window,
        isDark: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isDark) window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //实现状态栏图标和文字颜色为暗色
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun setFullScreen(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
        AnimeToast.makeText(App.context, this, duration).show()
    }

    fun CharSequence.showToastOnThread(duration: Int = Toast.LENGTH_SHORT) {
        GlobalScope.launch(Dispatchers.Main) {
            this@showToastOnThread.showToast(duration)
        }
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

    fun getFileSize(f: File): Long {
        var s: Long = 0
        if (f.exists() && f.isFile) {
            val fis = FileInputStream(f)
            s = fis.available().toLong()
        }
        return s
    }

    fun getDirectorySize(f: File): Long {
        var size: Long = 0
        val fList = f.listFiles()
        fList?.let {
            for (i in it.indices) {
                size += if (it[i].isDirectory) {
                    getDirectorySize(it[i])
                } else {
                    getFileSize(it[i])
                }
            }
        }
        return size
    }

    /**
     * 获取规整的文件大小
     * @param size 文件大小
     * @param newScale 精确到小数点几位
     */
    fun getFormatSize(size: Double, newScale: Int = 2): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "B"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
                .toString() + "K"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
                .toString() + "M"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
                .toString() + "G"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "T"
    }

    fun Drawable.toBitmap(): Bitmap {
        // 取 drawable 的长宽
        val w: Int = this.intrinsicWidth
        val h: Int = this.intrinsicHeight

        // 取 drawable 的颜色格式
        val config: Bitmap.Config =
            if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        // 建立对应 bitmap
        val bitmap: Bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        draw(canvas)
        return bitmap
    }

    fun String.isYearMonth(): Boolean {
        return Pattern.compile("[1-9][0-9]{3}(0[1-9]|1[0-2])").matcher(this).matches()
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
                    var detailPartUrl = actionUrl.substringAfter(Const.ActionUrl.ANIME_DETAIL, "")
                    if (detailPartUrl.isBlank()) App.context.getString(R.string.error_play_episode)
                        .showToast()
                    detailPartUrl = Const.ActionUrl.ANIME_DETAIL + detailPartUrl
                    activity.startActivity(
                        Intent(activity, PlayActivity::class.java)
                            .putExtra(
                                "partUrl",
                                actionUrl.substringBefore(Const.ActionUrl.ANIME_DETAIL)
                            )
                            .putExtra("detailPartUrl", detailPartUrl)
                    )
                } else {
                    App.context.getString(R.string.error_play_episode).showToast()
                }
            }
            decodeUrl.replace("/", "").isYearMonth() -> {     //如201907月新番列表
                activity.startActivity(
                    Intent(activity, MonthAnimeActivity::class.java)
                        .putExtra("partUrl", actionUrl)
                )
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_CLASSIFY) -> {     //如进入分类页面
                val paramList = actionUrl.replace(Const.ActionUrl.ANIME_CLASSIFY, "").split("/")
                if (paramList.size == 4) {      //例如  /japan/地区/日本  分割后是4个参数：""，japan，地区，日本
                    activity.startActivity(
                        Intent(activity, ClassifyActivity::class.java)
                            .putExtra("partUrl", "/" + paramList[1] + "/")
                            .putExtra("classifyTabTitle", paramList[2])
                            .putExtra("classifyTitle", paramList[3])
                    )
                } else App.context.resources.getString(R.string.action_url_format_error).showToast()
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_BROWSER) -> {     //打开浏览器
                openBrowser(actionUrl.replaceFirst(Const.ActionUrl.ANIME_BROWSER, ""))
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE) -> { //缓存的每一集列表
                val directoryName =
                    actionUrl.replaceFirst(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE, "")
                activity.startActivity(
                    Intent(activity, AnimeDownloadActivity::class.java)
                        .putExtra("mode", 1)
                        .putExtra("actionBarTitle", directoryName.replace("/", ""))
                        .putExtra("directoryName", directoryName)
                )
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY) -> { //播放缓存的每一集
                val filePath =
                    actionUrl.replaceFirst(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY + "/", "")
                val fileName = filePath.split("/").last()
                val title = fileName
                activity.startActivity(
                    Intent(activity, SimplePlayActivity::class.java)
                        .putExtra("url", "file://$filePath")
                        .putExtra("title", title)
                )
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_M3U8) -> { //播放缓存的每一集M3U8
                "暂不支持m3u8格式 :(".showToast(Toast.LENGTH_LONG)
            }
            decodeUrl.startsWith(Const.ActionUrl.ANIME_LAUNCH_ACTIVITY) -> { // 启动Activity
                val cls = Class.forName(
                    actionUrl.replaceFirst(Const.ActionUrl.ANIME_LAUNCH_ACTIVITY, "")
                        .split("/").last()
                )
                activity.startActivity(Intent(activity, cls))
            }
            else -> {
                "${toastTitle},${App.context.resources.getString(R.string.currently_not_supported)}".showToast()
            }
        }
    }

}