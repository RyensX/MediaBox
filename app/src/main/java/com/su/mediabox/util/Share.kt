package com.su.mediabox.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.pluginapi.action.WebBrowserAction
import com.su.mediabox.util.Util.copy2Clipboard

object Share {
    const val SHARE_QQ = 1
    const val SHARE_WECHAT = 2
    const val SHARE_WEIBO = 3
    const val SHARE_LINK = 4
    const val SHARE_WEB = 5

    fun isInstalled(packageName: String): Boolean {
        val packageInfo: PackageInfo? = try {
            App.context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return packageInfo != null
    }

    fun isQQInstalled() = isInstalled("com.tencent.mobileqq")

    fun isWechatInstalled() = isInstalled("com.tencent.mm")

    fun isWeiboInstalled() = isInstalled("com.sina.weibo")

    fun share(activity: Activity, shareContent: String, shareType: Int) {
        when (shareType) {
            SHARE_QQ -> {
                if (!isQQInstalled()) {
                    activity.resources.getString(R.string.not_install_qq).showToast()
                    return
                }
                startShare(
                    activity, shareContent,
                    "com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"
                )
            }
            SHARE_WECHAT -> {
                if (!isWechatInstalled()) {
                    activity.resources.getString(R.string.not_install_wechat).showToast()
                    return
                }
                startShare(
                    activity, shareContent,
                    "com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI"
                )
            }
            SHARE_WEIBO -> {
                if (!isWeiboInstalled()) {
                    activity.resources.getString(R.string.not_install_weibo).showToast()
                    return
                }
                startShare(
                    activity,
                    shareContent,
                    "com.sina.weibo",
                    "com.sina.weibo.composerinde.ComposerDispatchActivity"
                )
            }
            SHARE_LINK -> {
                shareContent.copy2Clipboard(activity)
                activity.resources.getString(R.string.already_copy_to_clipboard).showToast()
            }
            SHARE_WEB -> {
                WebBrowserAction.obtain(shareContent).go(activity)
            }
        }
    }

    private fun startShare(
        activity: Activity,
        shareContent: String,
        packageName: String? = null,
        className: String? = null,
    ) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareContent)
                if (packageName != null && className != null) setClassName(
                    packageName,
                    className
                )
            }
            activity.startActivity(shareIntent)
        } catch (e: Exception) {
            activity.resources.getString(R.string.share_unknown_error).showToast()
        }

    }
}
