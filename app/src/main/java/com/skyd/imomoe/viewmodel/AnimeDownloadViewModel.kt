package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.Util.directorySize
import com.skyd.imomoe.util.Util.fileSize
import com.skyd.imomoe.util.comparator.EpisodeTitleComparator
import com.skyd.imomoe.util.Util.getFormatSize
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.deleteAnimeFromXml
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.getAnimeFromXml
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper.Companion.save2Xml
import com.skyd.imomoe.util.toMD5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class AnimeDownloadViewModel : ViewModel() {
    var animeCoverList: MutableList<AnimeCoverBean> = ArrayList()
    var mldAnimeCoverList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAnimeCover() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = arrayOf(File(Const.DownloadAnime.animeFilePath).listFiles(),
                Const.DownloadAnime.run {
                    new = false
                    val f = File(animeFilePath).listFiles()
                    new = true
                    f
                })
            animeCoverList.clear()
            for (i: Int in 0..1) {
                files[i]?.let {
                    for (file in it) {
                        if (file.isDirectory) {
                            val episodeCount = file.listFiles { file, s ->
                                //查找文件名不以.temp结尾的文件
                                !s.endsWith(".temp") && !s.endsWith(".xml")
                            }?.size
                            animeCoverList.add(
                                animeCoverList.size, AnimeCoverBean(
                                    Const.ViewHolderTypeString.ANIME_COVER_7,
                                    Const.ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE + "/" + file.name,
                                    "",
                                    file.name,
                                    null,
                                    "",
                                    size = getFormatSize(file.directorySize().toDouble()),
                                    episodeCount = episodeCount.toString() + "P",
                                    path = if (i == 0) 0 else 1
                                )
                            )
                        }
                    }
                }
            }
            mldAnimeCoverList.postValue(true)
        }
    }

    fun getAnimeCoverEpisode(directoryName: String, path: Int = 0) {
        //不支持重命名文件
        viewModelScope.launch(Dispatchers.IO) {
            val animeFilePath = if (path == 0) Const.DownloadAnime.animeFilePath
            else {
                Const.DownloadAnime.new = false
                val p = Const.DownloadAnime.animeFilePath
                Const.DownloadAnime.new = true
                p
            }
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
                    val fileName =
                        animeFilePath + directoryName.substring(1, directoryName.length) +
                                "/" + anime.fileName
                    animeCoverList.add(
                        AnimeCoverBean(
                            Const.ViewHolderTypeString.ANIME_COVER_7,
                            (if (fileName.endsWith(".m3u8", true))
                                Const.ActionUrl.ANIME_ANIME_DOWNLOAD_M3U8
                            else Const.ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY)
                                    + "/" + fileName,
                            "",
                            anime.title,
                            null,
                            "",
                            size = getFormatSize(
                                File(animeFilePath + directoryName + "/" + anime.fileName)
                                    .fileSize().toDouble()
                            ),
                            path = path
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