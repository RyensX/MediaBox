package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.Util.openBrowser
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*

class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        iv_toolbar_1_back.setOnClickListener { finish() }
        tv_toolbar_1_title.text = getString(R.string.about)

        tv_about_activity_version.text = "V " + getAppVersionName()

        rl_about_activity_imomoe.setOnClickListener {
            openBrowser(Api.MAIN_URL)
        }

        rl_about_activity_github.setOnClickListener {
            openBrowser("https://github.com/SkyD666/Imomoe")
        }

        rl_about_activity_license.setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }
    }
}