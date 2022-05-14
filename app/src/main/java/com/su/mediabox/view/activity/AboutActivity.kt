package com.su.mediabox.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityAboutBinding
import com.su.mediabox.util.Util
import com.su.mediabox.util.Util.getAppVersionCode
import com.su.mediabox.util.Util.getAppVersionName
import com.su.mediabox.util.Util.openBrowser
import com.su.mediabox.util.visible
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
                    message(res = R.string.statement)
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

            tvAboutActivityVersion.text = String.format("%s(%s)",
                getAppVersionName(),
                getAppVersionCode()
            )

            //UP_TODO 2022/2/14 15:10 0 暂时移除检查更新
            /**
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
                        text = pluginConfig.run {
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
            */

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

        }
    }

    override fun getBinding(): ActivityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
}