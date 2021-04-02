package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivityAboutBinding
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.openBrowser
import com.skyd.imomoe.util.visible
import java.util.*

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            llAboutActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }
            llAboutActivityToolbar.tvToolbar1Title.text = getString(R.string.about)
            llAboutActivityToolbar.ivToolbar1Button1.visible()
            llAboutActivityToolbar.ivToolbar1Button1.setImageResource(R.drawable.ic_info_white_24)
            llAboutActivityToolbar.ivToolbar1Button1.setOnClickListener {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.attention)
                    message(text = "本软件免费开源，严禁商用，支持Android 5.0+！仅在Github和Gitee仓库发布！\n不介意的话可以给我的Github仓库点个Star")
                    positiveButton(text = "去点Star") { openBrowser(Const.Common.GITHUB_URL) }
                    negativeButton(text = "关闭") { dismiss() }
                }
            }

            val c: Calendar = Calendar.getInstance()
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            if (month == Calendar.DECEMBER && day == 25) {     // 圣诞节彩蛋
                ivAboutActivityIconEgg.visible()
                ivAboutActivityIconEgg.setImageResource(R.drawable.ic_christmas_hat)
            }

            tvAboutActivityVersion.text = "V " + getAppVersionName()

            rlAboutActivityImomoe.setOnClickListener {
                openBrowser(Api.MAIN_URL)
            }

            rlAboutActivityGithub.setOnClickListener {
                openBrowser(Const.Common.GITHUB_URL)
            }

            rlAboutActivityGitee.setOnClickListener {
                openBrowser(Const.Common.GITEE_URL)
            }

            rlAboutActivityLicense.setOnClickListener {
                startActivity(Intent(this@AboutActivity, LicenseActivity::class.java))
            }

            rlAboutActivityTestDevice.setOnClickListener {
                MaterialDialog(this@AboutActivity).show {
                    title(res = R.string.test_device)
                    message(text = "HONOR V20 Android 9\n别骂了别骂了QAQ，太穷了，没有Android 10/11的手机")
                    positiveButton(text = "确定") { dismiss() }
                }
            }
        }
    }

    override fun getBinding(): ActivityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
}