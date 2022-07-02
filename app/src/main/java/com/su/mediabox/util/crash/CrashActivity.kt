package com.su.mediabox.util.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.analytics.Analytics
import com.su.appcrashhandler.AppCatchException
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityCrashBinding
import com.su.mediabox.util.Util
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.viewBind
import com.su.mediabox.view.activity.MainActivity
import kotlin.system.exitProcess

class CrashActivity : AppCompatActivity() {

    private val viewBinding by viewBind(ActivityCrashBinding::inflate)

    fun getErrorInfo() = intent.getStringExtra(AppCatchException.INFO_ERROR)

    fun getPhoneInfo() = intent.getStringExtra(AppCatchException.INFO_PHONE)

    fun getVersionInfo() = intent.getStringExtra(AppCatchException.INFO_VERSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.crash_title)
        viewBinding.apply {
            caVersion.text = getVersionInfo()
            caCrashInfo.text = getErrorInfo()
            caSystemInfo.text = getPhoneInfo()

            caReport.setOnClickListener {
                val cm =
                    this@CrashActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(
                    ClipData.newPlainText("MediaBoxCrashInfo", crashInfo())
                )
                Util.openBrowser(Const.Common.GITHUB_NEW_ISSUE_URL)
            }
        }
    }

    private fun crashInfo() =
        "Version:${getVersionInfo()}\nCrash:${getErrorInfo()}\nPhone:${getPhoneInfo()}\n"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.crash_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //重启
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goActivity<MainActivity>()
        this@CrashActivity.finish()
        return super.onOptionsItemSelected(item)
    }
}