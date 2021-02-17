package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.MD5.getMD5
import com.skyd.imomoe.util.Util.getDirectorySize
import com.skyd.imomoe.util.Util.getFileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class AnimeDownloadViewModel : ViewModel() {
    var animeCoverList: MutableList<AnimeCoverBean> = ArrayList()
    var mldAnimeCoverList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAnimeCover() {
        GlobalScope.launch(Dispatchers.IO) {
            val files = File(Const.DownloadAnime.animeFilePath).listFiles()
            files?.let {
                animeCoverList.clear()
                for (file in it) {
                    if (file.isDirectory) {
                        val episodeCount = file.listFiles { file, s ->
                            //查找文件名不以.temp结尾的文件
                            !s.endsWith(".temp")
                        }?.size
                        animeCoverList.add(
                            animeCoverList.size, AnimeCoverBean(
                                "animeCover7",
                                Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE + "/" + file.name,
                                "",
                                file.name,
                                "",
                                "",
                                size = String.format(
                                    "%.1fM",
                                    getDirectorySize(file) / 1048576.0      //1024*1024==1048576
                                ),
                                episodeCount = episodeCount.toString() + "P"
                            )
                        )
                    }
                }
            }
            mldAnimeCoverList.postValue(true)
        }
    }

    fun getAnimeCoverEpisode(directoryName: String) {
        val files = File(Const.DownloadAnime.animeFilePath + directoryName).listFiles()

        GlobalScope.launch(Dispatchers.IO) {
            files?.let {
                animeCoverList.clear()
                for (file in it) {
                    if (file.isFile) {
                        val animeDownloadEntity = getAppDataBase().animeDownloadDao()
                            .getAnimeDownload(getMD5(file) ?: "")
                        if (animeDownloadEntity == null && file.extension == "temp") continue
                        val title = animeDownloadEntity?.title ?: file.name
                        animeCoverList.add(
                            animeCoverList.size, AnimeCoverBean(
                                "animeCover7",
                                (if (file.path.endsWith(".m3u8", true))
                                    Const.ActionUrl.ANIME_ANIME_DOWNLOAD_M3U8
                                else Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY)
                                        + "/" + file.path,
                                "",
                                title,
                                "",
                                "",
                                size = String.format(
                                    "%.1fM",
                                    getFileSize(file) / 1048576.0      //1024*1024==1048576
                                )
                            )
                        )
                    }
                }
                mldAnimeCoverList.postValue(true)
            }
        }
    }

    companion object {
        const val TAG = "AnimeDownloadViewModel"
    }
}