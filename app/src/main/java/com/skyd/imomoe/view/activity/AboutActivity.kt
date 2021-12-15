package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivityAboutBinding
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.openBrowser
import com.skyd.imomoe.util.visible
import java.net.URL
import java.util.*

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            atbAboutActivity.setBackButtonClickListener { finish() }
            atbAboutActivity.setButtonClickListener(0) {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.attention)
                    message(text = "本软件免费开源，严禁商用，支持Android 5.0+！仅在Github仓库长期发布！\n不介意的话可以给我的Github仓库点个Star")
                    positiveButton(text = "去点Star") { openBrowser(Const.Common.GITHUB_URL) }
                    negativeButton(res = R.string.cancel) { dismiss() }
                }
            }

            val c: Calendar = Calendar.getInstance()
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            if (month == Calendar.DECEMBER && (day > 21 || day < 29)) {     // 圣诞节彩蛋
                ivAboutActivityIconEgg.visible()
                ivAboutActivityIconEgg.setImageResource(R.drawable.ic_christmas_hat)
            }

            tvAboutActivityVersion.text = getAppVersionName()

            rlAboutActivityImomoe.setOnClickListener {
                var warningString: String = getString(R.string.jump_to_data_source_website_warning)
                if (URL(Api.MAIN_URL).protocol == "http") {
                    warningString =
                        getString(R.string.jump_to_browser_http_warning) + "\n" + warningString
                }
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.warning)
                    message(text = warningString)
                    positiveButton(res = R.string.ok) { openBrowser(Api.MAIN_URL) }
                    negativeButton { dismiss() }
                }
            }

            ivAboutActivityCustomDataSourceAbout.setOnClickListener {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.data_source_info)
                    message(
                        text = (DataSourceManager.getConst()
                            ?: com.skyd.imomoe.model.impls.Const()).run {
                            "${
                                getString(
                                    R.string.data_source_jar_version_name,
                                    versionName()
                                )
                            }\n${
                                getString(
                                    R.string.data_source_jar_version_code,
                                    versionCode().toString()
                                )
                            }\n${about()}"
                        }
                    )
                    positiveButton(res = R.string.ok) { dismiss() }
                }
            }

            rlAboutActivityGithub.setOnClickListener {
                openBrowser(Const.Common.GITHUB_URL)
            }

            rlAboutActivityLicense.setOnClickListener {
                startActivity(Intent(this@AboutActivity, LicenseActivity::class.java))
            }

            rlAboutActivityUserNotice.setOnClickListener {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.user_notice)
                    message(text = Html.fromHtml(Util.getUserNoticeContent()))
                    cancelable(false)
                    positiveButton(res = R.string.ok) {
                        Util.setReadUserNoticeVersion(Const.Common.USER_NOTICE_VERSION)
                    }
                }
            }

            rlAboutActivityTestDevice.setOnClickListener {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.test_device)
                    message(
                        text = "Physical Device: \nAndroid 10\n\n" +
                                "Virtual Machine: \nPixel Android 5\n" +
                                "雷电模拟器4.0.63 Android 7.1.2\n" +
                                "Pixel 2 Android 8\n" +
                                "Pixel 3 Android 9\n"
                    )
                    positiveButton(res = R.string.ok) { dismiss() }
                }
            }
        }
    }

    override fun getBinding(): ActivityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
}