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

@Deprecated("需要重新实现")
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

    }

    companion object {
        const val TAG = "AnimeDownloadViewModel"
    }
}