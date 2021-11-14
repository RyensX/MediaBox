package com.skyd.imomoe.util

import android.app.Activity
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.config.UnknownActionUrl
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.RouteProcessor
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.view.component.AnimeToast
import com.skyd.skin.SkinManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
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

    fun getEncodedUrl(url: String): String {
        return Uri.encode(url, ":/-![].,%?&=")
    }

    /**
     * 通过播放页面的网址获取详情页面的网址
     *
     * @param episodeUrl 播放页面的网址
     * @return 详情页面的网址
     */
    fun getDetailLinkByEpisodeLink(episodeUrl: String): String {
        return (DataSourceManager.getUtil()
            ?: com.skyd.imomoe.model.impls.Util()).getDetailLinkByEpisodeLink(episodeUrl)
    }

    fun restartApp() {
        val i = App.context.packageManager.getLaunchIntentForPackage(App.context.packageName)
        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        App.context.startActivity(i)
    }

    /**
     * 上次读过的用户须知的版本号
     */
    fun lastReadUserNoticeVersion(): Int = App.context.sharedPreferences().getInt("userNotice", 0)

    /**
     * @param version 用户须知版本号
     */
    fun setReadUserNoticeVersion(version: Int) = App.context.sharedPreferences().editor {
        putInt("userNotice", version)
    }

    /**
     * 获取用户须知String
     */
    fun getUserNoticeContent(): String {
        val sb = StringBuffer()
        try {
            val inputStream = App.context.resources.openRawResource(R.raw.notice)
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var out: String?
            while (reader.readLine().also { out = it } != null) {
                sb.append(out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    fun getWebsiteLinkSuffix(): String {
        return App.context.sharedPreferences().getString("websiteLinkSuffix", ".html") ?: ".html"
    }

    fun setWebsiteLinkSuffix(suffix: String) {
        App.context.sharedPreferences().editor {
            putString("websiteLinkSuffix", suffix)
        }
    }

    fun openVideoByExternalPlayer(context: Context, url: String): Boolean {
        return try {
            val uri: Uri =
                if (url.startsWith("file:///")) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        FileProvider.getUriForFile(
                            context,
                            "${context.applicationInfo.packageName}.fileProvider",
                            File(
                                url.substring(0, url.lastIndexOf("/")).replaceFirst("file:///", ""),
                                url.substring(url.lastIndexOf("/") + 1, url.length)
                            )
                        )
                    } else {
                        Uri.parse(url)
                    }
                } else Uri.parse(url)

            Intent().setAction(Intent.ACTION_VIEW).addFlags(FLAG_ACTIVITY_NEW_TASK)
                .addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "video/*").apply {
                    context.startActivity(
                        Intent.createChooser(
                            this, context.getString(R.string.choose_video_player)
                        )
                    )
                }
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }


    /**
     * 由于SUNDAY == 1...，因此需要转换成SUNDAY == 7...
     * @param day Calendar中的日期
     */
    fun getRealDayOfWeek(day: Int) = if (day == 1) 7 else day - 1

    /**
     * 返回星期几
     * @param day Calendar中的日期
     */
    fun getWeekday(day: Int): String {
        return if (day == 1) "星期天" else when (day - 1) {
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            else -> "星期六"
        }
    }

    /**
     * 获取系统屏幕亮度
     */
    fun getScreenBrightness(activity: Activity): Int? = try {
        Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
        null
    }

    /**
     * 更改Drawable颜色
     */
    fun tintDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }

    /**
     * 获取重定向最终的地址
     * @param path
     */
    fun getRedirectUrl(path: String, referer: String = ""): String {
        var url = path
        return try {
            var conn: HttpURLConnection
            do {
                conn = URL(url).openConnection() as HttpURLConnection
                conn.setRequestProperty("Referer", referer)
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
     * 通过原始id获取当前皮肤的id
     */
    fun getSkinResourceId(@AnyRes id: Int) = SkinManager.getSkinResourceId(id)

    /**
     * 通过id获取drawable
     */
    fun getResDrawable(@DrawableRes id: Int) = SkinManager.getDrawableOrMipMap(id)

    /**
     * 通过id获取颜色
     */
    fun Context.getResColor(@ColorRes id: Int) = SkinManager.getColor(id)

    /**
     * 通过id获取颜色，不随皮肤更改，使用默认的
     */
    fun Context.getDefaultResColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

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

    fun String.copy2Clipboard(context: Context) {
        try {
            val systemService: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            systemService.setPrimaryClip(ClipData.newPlainText("text", this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNewVersion(version: String): Boolean {
        val currentVersion = getAppVersionName()
        return try {
            version != currentVersion &&
                    version.replaceFirst("v", "", true) != currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            "检查版本号失败，建议手动到Github查看是否有更新\n当前版本：$currentVersion".showToast(Toast.LENGTH_LONG)
            false
        }
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

    fun getManifestMetaValue(name: String): String {
        var metaValue = ""
        try {
            val packageManager = App.context.packageManager
            if (packageManager != null) {
                // 注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                val applicationInfo = packageManager.getApplicationInfo(
                    App.context.packageName,
                    PackageManager.GET_META_DATA
                )
                if (applicationInfo.metaData != null) {
                    metaValue = applicationInfo.metaData[name].toString()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return metaValue
    }

    fun EditText.showKeyboard() {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        val inputManager =
            App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }

    fun EditText.hideKeyboard() {
        val inputManager =
            App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.windowToken, 0)
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

    val Float.dp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )

    val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()

    val Float.sp: Float                 // [xxhdpi](360 -> 1080)
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        )

    val Int.sp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()

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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = statusBarColor
        }
    }

    fun getStatusBarHeight(): Int {
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

    fun CharSequence.showToastOnIOThread(duration: Int = Toast.LENGTH_SHORT) {
        GlobalScope.launch(Dispatchers.Main) {
            this@showToastOnIOThread.showToast(duration)
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

    fun File.fileSize(): Long {
        var s: Long = 0
        if (this.exists() && this.isFile) {
            val fis = FileInputStream(this)
            s = fis.available().toLong()
        }
        return s
    }

    fun File.directorySize(): Long {
        var size: Long = 0
        val fList = listFiles()
        fList?.let {
            for (i in it.indices) {
                size += if (it[i].isDirectory) {
                    it[i].directorySize()
                } else {
                    it[i].fileSize()
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
        return result4.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString()
            .toString() + "T"
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
        actionUrl ?: return
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val routerProcessor = DataSourceManager.getRouterProcessor() ?: RouteProcessor()
        // 没有处理跳转，则进入if体
        if (!routerProcessor.process(activity, actionUrl)) {
            when {
                decodeUrl.startsWith(Const.ActionUrl.ANIME_BROWSER) -> {     //打开浏览器
                    openBrowser(actionUrl.replaceFirst(Const.ActionUrl.ANIME_BROWSER, ""))
                }
                decodeUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE) -> { //缓存的每一集列表
                    var directoryName: String
                    var path: Int
                    actionUrl.replaceFirst(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE, "")
                        .split("/").let {
                            directoryName = it[0] + "/" + it[1]
                            path = it.last().toInt()
                        }
                    activity.startActivity(
                        Intent(activity, AnimeDownloadActivity::class.java)
                            .putExtra("mode", 1)
                            .putExtra("actionBarTitle", directoryName.replace("/", ""))
                            .putExtra("directoryName", directoryName)
                            .putExtra("path", path)
                    )
                }
                decodeUrl.startsWith(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY) -> { //播放缓存的每一集
                    val filePath =
                        actionUrl.replaceFirst(Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY + "/", "")
                            .replace(Regex("/\\d+$"), "")
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
                decodeUrl.startsWith(Const.ActionUrl.ANIME_SKIP_BY_WEBSITE) -> { // 根据网址跳转
                    var website = decodeUrl.replaceFirst(Const.ActionUrl.ANIME_SKIP_BY_WEBSITE, "")
                    if (website.isBlank() || website == "/") {
                        MaterialDialog(activity).input(hintRes = R.string.input_a_website) { dialog, text ->
                            try {
                                var url = text.toString()
                                if (!url.matches(Regex("^.+://.*"))) url = "http://$url"
                                process(activity, URL(url).file)
                            } catch (e: Exception) {
                                App.context.resources.getString(R.string.website_format_error)
                                    .showToast()
                                e.printStackTrace()
                            }
                        }.positiveButton(R.string.ok).show()
                    } else {
                        try {
                            if (!website.matches(Regex("^.+://.*"))) website = "http://$website"
                            process(activity, URL(website).file)
                        } catch (e: Exception) {
                            App.context.resources.getString(R.string.website_format_error)
                                .showToast()
                            e.printStackTrace()
                        }
                    }
                }
                else -> {
                    val action = UnknownActionUrl.actionMap[decodeUrl]
                    if (action != null) {
                        action.action()
                    } else {
                        // 空内容
                        if (decodeUrl.isBlank()) return
                        App.context.resources.getString(
                            R.string.unknown_route,
                            if (toastTitle.isBlank()) actionUrl else toastTitle
                        ).showToast()
                    }
                }
            }
        }
    }

    fun process(context: Context, actionUrl: String?, toastTitle: String = "") {
        actionUrl ?: return
        val decodeUrl = URLDecoder.decode(actionUrl, "UTF-8")
        val routerProcessor = DataSourceManager.getRouterProcessor() ?: RouteProcessor()
        // 没有处理跳转，则进入if体
        if (!routerProcessor.process(context, actionUrl)) {
            when {
                decodeUrl.startsWith(Const.ActionUrl.ANIME_BROWSER) -> {     //打开浏览器
                    openBrowser(actionUrl.replaceFirst(Const.ActionUrl.ANIME_BROWSER, ""))
                }
                decodeUrl.startsWith(Const.ActionUrl.ANIME_LAUNCH_ACTIVITY) -> { // 启动Activity
                    val cls = Class.forName(
                        actionUrl.replaceFirst(Const.ActionUrl.ANIME_LAUNCH_ACTIVITY, "")
                            .split("/").last()
                    )
                    context.startActivity(Intent(context, cls).addFlags(FLAG_ACTIVITY_NEW_TASK))
                }
                decodeUrl.startsWith(Const.ActionUrl.ANIME_NOTICE) -> { // 显示通知
                    val paramString: String =
                        actionUrl.replaceFirst(Const.ActionUrl.ANIME_NOTICE, "")
                            .split("?").run {
                                if (!isEmpty()) last()
                                else ""
                            }
                    if (paramString.isBlank()) {
                        App.context.getString(R.string.notice_activity_error_param).showToast()
                        return
                    }
                    context.startActivity(
                        Intent(context, NoticeActivity::class.java)
                            .putExtra(NoticeActivity.PARAM, paramString)
                            .addFlags(FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                else -> {
                    if (decodeUrl.isBlank()) return
                    App.context.resources.getString(
                        R.string.unknown_route,
                        if (toastTitle.isBlank()) actionUrl else toastTitle
                    ).showToast()
                }
            }
        }
    }
}