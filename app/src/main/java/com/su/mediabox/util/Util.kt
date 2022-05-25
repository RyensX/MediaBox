package com.su.mediabox.util

import android.app.Activity
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.su.mediabox.util.logD
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
import com.su.mediabox.App
import com.su.mediabox.R
import okhttp3.internal.and
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.jar.JarFile


object Util {

    fun openBrowser(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        App.context.startActivity(intent)
    }

    fun String.toEncodedUrl(): String {
        return Uri.encode(this, ":/-![].,%?&=")
    }

    @Deprecated(
        "use String.toEncodedUrl()",
        ReplaceWith("url.toEncodedUrl()", "com.skyd.imomoe.util.Util.toEncodedUrl")
    )
    fun getEncodedUrl(url: String): String = url.toEncodedUrl()

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
            val context = App.context
            //不直接引用以方便CI构建Debug Artifact
            val noticeResId = context.resources.getIdentifier("notice", "raw", context.packageName)
            if (noticeResId == 0)
                return ""
            val inputStream = context.resources.openRawResource(noticeResId)
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
     * 通过id获取颜色
     */
    fun getResColor(@ColorRes id: Int) = ContextCompat.getColor(App.context, id)

    /**
     * 通过id获取drawable
     */
    fun getResDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(App.context, id)

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

    fun isNewVersionByVersionCode(version: String): Boolean {
        val currentVersion = getAppVersionCode().toString()
        return try {
            version != currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            "检查版本号失败，建议手动到Github查看是否有更新\n当前版本代码：$currentVersion".showToast(Toast.LENGTH_LONG)
            false
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

    /**
     * 获取apk包签名基本信息
     * @return string[0]证书发行者,string[1]证书所有者,string[2]序列号，string[3]证书起始时间 string[4]证书结束时间
     */
    fun getApkSignInfo(filePath: String): Array<String> {
        var subjectDN = ""
        var issuerDN = ""
        var serial = ""
        var notBefore = ""
        var notAfter = ""
        try {
            val JarFile = JarFile(filePath)
            val jarEntry = JarFile.getJarEntry("AndroidManifest.xml")
            if (jarEntry != null) {
                val readBuffer = ByteArray(8192)
                val ins = BufferedInputStream(JarFile.getInputStream(jarEntry))
                while (ins.read(readBuffer, 0, readBuffer.size) !== -1);
                val certs = jarEntry.certificates
                if (certs != null && certs.isNotEmpty()) {
                    //获取证书
                    val x509cert: X509Certificate = certs[0] as X509Certificate
                    //获取证书发行者
                    issuerDN = x509cert.issuerDN.toString()
                    //System.out.println("发行者：" + issuerDN);
                    //获取证书所有者
                    subjectDN = x509cert.subjectDN.toString()
                    //System.out.println("所有者：" + subjectDN);
                    //证书序列号
                    serial = x509cert.serialNumber.toString()
                    //System.out.println("publicKey：" + publicKey);
                    //证书起始有效期
                    notBefore = x509cert.notBefore.toString()
                    //证书结束有效期
                    notAfter = x509cert.notAfter.toString()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return arrayOf(subjectDN, issuerDN, serial, notBefore, notAfter)
    }

    fun getSignatures(packageInfo: PackageInfo): String {
        return try {
            //获取签名信息
            val cert: ByteArray = packageInfo.signatures[0].toByteArray()
            //将签名转换为字节数组流
            val input: InputStream = ByteArrayInputStream(cert)
            //证书工厂类，这个类实现了出厂合格证算法的功能
            val cf: CertificateFactory = CertificateFactory.getInstance("X509")
            //X509 证书，X.509 是一种非常通用的证书格式
            val c = cf.generateCertificate(input) as X509Certificate
            //加密算法的类，这里的参数可以使 MD5 等加密算法
            val md: MessageDigest = MessageDigest.getInstance("SHA1")
            //获得公钥
            val publicKey: ByteArray = md.digest(c.encoded)
            //字节到十六进制的格式转换
            val str = StringBuilder()
            var temp: String
            for (i in publicKey.indices) {
                temp = Integer.toHexString(publicKey[i] and 0xFF)
                str.append(temp)
            }
            str.deleteCharAt(str.length - 1)
            str.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 异常则返回null
     */
    inline fun <T> withoutExceptionGet(
        showErrToast: Boolean = false,
        showErrMsg: Boolean = true,
        block: () -> T?
    ) = try {
        block()
    } catch (e: Exception) {
        if (showErrMsg) {
            logD("取值错误", e.message ?: "")
            e.printStackTrace()
        }
        if (showErrToast)
            e.message?.showToast()
        null
    }

    fun Context.openUrl(url: String) {
        val uri = Uri.parse(url)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}