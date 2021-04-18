package com.skyd.imomoe.view.activity

import android.content.*
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.skyd.imomoe.config.Const.Common.Companion.GITHUB_NEW_ISSUE_URL
import com.skyd.imomoe.util.Util.openBrowser
import kotlin.system.exitProcess


/**
 * 调试用的异常activity，不要继承BaseActivity
 */
class CrashActivity : AppCompatActivity() {
    companion object {
        const val CRASH_INFO = "crashInfo"

        fun start(context: Context, crashInfo: String) {
            val intent = Intent(context, CrashActivity::class.java)
            intent.putExtra(CRASH_INFO, crashInfo)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashInfo = intent.getStringExtra(CRASH_INFO)

        val message = "CrashInfo:\n$crashInfo"
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setTitle("哦呼，樱花动漫崩溃了！快去Github提Issue吧")
            setPositiveButton("复制信息打开Github") { _: DialogInterface, i: Int ->
                val cm =
                    this@CrashActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("exception trace stack", message))
                openBrowser(GITHUB_NEW_ISSUE_URL)
                this@CrashActivity.finish()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
            setNegativeButton("退出") { _: DialogInterface, i: Int ->
                this@CrashActivity.finish()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
            setCancelable(false)
            setFinishOnTouchOutside(false)
        }.show()
    }
}