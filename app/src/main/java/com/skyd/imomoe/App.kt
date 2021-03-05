package com.skyd.imomoe

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.liulishuo.filedownloader.FileDownloader
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.sharedPreferences
import com.skyd.imomoe.util.update.AppUpdateHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        FileDownloader.setup(this)

        //夜间模式
        AppCompatDelegate.setDefaultNightMode(
            if (sharedPreferences("nightMode").getBoolean("isNightMode", false))
                AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        lateinit var context: Context
    }
}