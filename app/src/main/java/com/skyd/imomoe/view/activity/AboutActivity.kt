package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivityAboutBinding
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.openBrowser

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            llAboutActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }
            llAboutActivityToolbar.tvToolbar1Title.text = getString(R.string.about)

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
        }
    }

    override fun getBinding(): ActivityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
}