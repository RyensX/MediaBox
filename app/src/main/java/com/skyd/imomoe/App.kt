package com.skyd.imomoe

import android.app.Application
import android.content.Context
import com.liulishuo.filedownloader.FileDownloader
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import java.io.File

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        FileDownloader.setup(this)
//        deleteUnCompletedDownload()
    }

    //删除正在下载时，应用被杀死，留下的下载不全的文件
//    private fun deleteUnCompletedDownload() {
//        Thread {
//            val list = getAppDataBase().animeDownloadDao().getAnimeDownloadList()
//            for (key in list) {
//                val file = File(Const.DownloadAnime.animeFilePath + key.key)
//                if (file.exists()) {
//                    file.delete()
//                }
//            }
//            getAppDataBase().animeDownloadDao().deleteAllAnimeDownload()
//        }.start()
//    }

    companion object {
        lateinit var context: Context
    }
}