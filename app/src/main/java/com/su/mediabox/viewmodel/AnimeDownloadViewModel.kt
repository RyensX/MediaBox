package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.config.Const
import com.su.mediabox.database.entity.AnimeDownloadEntity
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.util.comparator.EpisodeTitleComparator
import com.su.mediabox.util.downloadanime.AnimeDownloadHelper.Companion.deleteAnimeFromXml
import com.su.mediabox.util.downloadanime.AnimeDownloadHelper.Companion.getAnimeFromXml
import com.su.mediabox.util.downloadanime.AnimeDownloadHelper.Companion.save2Xml
import com.su.mediabox.util.formatSize
import com.su.mediabox.util.toMD5
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AnimeDownloadViewModel : ViewModel() {

    var mode = 0        //0是默认的，是番剧；1是番剧每一集
    var actionBarTitle = ""
    var directoryName = ""

    var animeCoverList: MutableList<AnimeCoverBean> = ArrayList()
    var mldAnimeCoverList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAnimeCover() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = File(Const.DownloadAnime.animeFilePath).listFiles()
            animeCoverList.clear()
            files?.forEach { file ->
                if (file.isDirectory) {
                    val episodeCount = file.listFiles { file, s ->
                        //查找文件名不以.temp结尾的文件
                        !s.endsWith(".temp") && !s.endsWith(".xml")
                    }?.size
                    val action = buildRouteActionUrl(
                        Constant.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE,
                        "1",
                        file.name,
                        file.name
                    )
                    animeCoverList.add(
                        animeCoverList.size, AnimeCoverBean(
                            Constant.ViewHolderTypeString.ANIME_COVER_7,
                            action,
                            "",
                            file.name,
                            null,
                            "",
                            size = file.formatSize(),
                            episodeCount = episodeCount.toString() + "P",
                        )
                    )
                }
            }
            mldAnimeCoverList.postValue(true)
        }
    }

    fun getAnimeCoverEpisode(directoryName: String) {
        //不支持重命名文件
        viewModelScope.launch(Dispatchers.IO) {
            val animeFilePath = Const.DownloadAnime.animeFilePath
            val files = File(animeFilePath + directoryName).listFiles()
            animeCoverList.clear()
            files?.let {
                val animeList = getAnimeFromXml(directoryName, animeFilePath)

                // xml里的文件名
                val animeFilesName: MutableList<String?> = ArrayList()
                // 文件夹下的文件名
                val filesName: MutableList<String> = ArrayList()
                // 获取文件夹下的文件名
                for (file in it) filesName.add(file.name)
                //数据库中的数据
                val animeMd5InDB = getAppDataBase().animeDownloadDao().getAnimeDownloadMd5List()
                // 先删除xml里被用户删除的视频，再获取xml里的文件名（保证xml里的文件名都是存在的文件）
                val iterator: MutableIterator<AnimeDownloadEntity> = animeList.iterator()
                while (iterator.hasNext()) {
                    val anime = iterator.next()
                    if (anime.fileName !in filesName) {
                        deleteAnimeFromXml(directoryName, anime, animeFilePath)
                        iterator.remove()
                    } else {
                        // 如果不在数据库中，则加入数据库
                        if (anime.md5 !in animeMd5InDB) {
                            getAppDataBase().animeDownloadDao().insertAnimeDownload(anime)
                        }
                        animeFilesName.add(anime.fileName)
                    }
                }
                // 没有在xml里的视频
                for (file in it) {
                    if (file.name !in animeFilesName) {
                        // 试图从数据库中取出不在xml里的视频的数据，如果没找到则是null
                        val unsavedAnime: AnimeDownloadEntity? =
                            getAppDataBase().animeDownloadDao()
                                .getAnimeDownload(file.toMD5() ?: "")
                        if (unsavedAnime != null && unsavedAnime.fileName == null) {
                            unsavedAnime.fileName = file.name
                            getAppDataBase().animeDownloadDao()
                                .updateFileNameByMd5(unsavedAnime.md5, file.name)
                        }
                        if (unsavedAnime != null) {
                            save2Xml(directoryName, unsavedAnime, animeFilePath)
                            animeList.add(unsavedAnime)
                        }
                    }
                }

                for (anime in animeList) {
                    val filePath = animeFilePath + directoryName + "/" + anime.fileName
                    animeCoverList.add(
                        AnimeCoverBean(
                            Constant.ViewHolderTypeString.ANIME_COVER_7,
                            (if (filePath.endsWith(".m3u8", true))
                                Constant.ActionUrl.ANIME_ANIME_DOWNLOAD_M3U8
                            else buildRouteActionUrl(
                                Constant.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY,
                                "file://$filePath", anime.title
                            )),
                            "",
                            anime.title,
                            null,
                            "",
                            size = File(filePath).formatSize(),
                        )
                    )
                }
                animeCoverList.sortWith(EpisodeTitleComparator())
            }
            mldAnimeCoverList.postValue(true)
        }
    }

    companion object {
        const val TAG = "AnimeDownloadViewModel"
    }
}