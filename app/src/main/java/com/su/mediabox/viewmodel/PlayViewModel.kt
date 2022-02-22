package com.su.mediabox.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.*
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.util.showToast
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.been.AnimeEpisodeDataBean
import com.su.mediabox.pluginapi.been.IAnimeDetailBean
import com.su.mediabox.pluginapi.been.PlayBean
import com.su.mediabox.pluginapi.components.IPlayComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlayViewModel : ViewModel() {
    private val playModel: IPlayComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IPlayComponent::class.java)
    }
    var playBean: PlayBean? = null

    var partUrl = ""
    var animeCover = ""
    var detailPartUrl = ""

    var mldPlayBean: MutableLiveData<PlayBean> = MutableLiveData()
    var playBeanDataList: MutableList<IAnimeDetailBean> = ArrayList()
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    var currentEpisodeIndex = 0
    val mldEpisodesList: MutableLiveData<Boolean> = MutableLiveData()
    val animeEpisodeDataBean = AnimeEpisodeDataBean("animeEpisode1", "", "")
    val mldAnimeEpisodeDataRefreshed: MutableLiveData<Boolean> = MutableLiveData()
    val mldGetAnimeEpisodeData: MutableLiveData<Int> = MutableLiveData()

    fun refreshAnimeEpisodeData(partUrl: String, currentEpisodeIndex: Int, title: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                playModel.refreshAnimeEpisodeData(partUrl, animeEpisodeDataBean).apply {
                    if (this) {
                        animeEpisodeDataBean.title = title
                        mldAnimeEpisodeDataRefreshed.postValue(true)
                    } else {
                        throw RuntimeException("html play class not found")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                animeEpisodeDataBean.actionUrl = "animeEpisode1"
                animeEpisodeDataBean.title = ""
                animeEpisodeDataBean.videoUrl = ""
                mldAnimeEpisodeDataRefreshed.postValue(false)
                "${App.context.getString(R.string.get_data_failed)}\n${e.message}".showToast()
            }
            this@PlayViewModel.currentEpisodeIndex = currentEpisodeIndex
        }
    }

    fun getAnimeEpisodeUrlData(partUrl: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                this@PlayViewModel.partUrl = partUrl
                playModel.getAnimeEpisodeUrlData(partUrl).apply {
                    this ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
                    episodesList[position].videoUrl = this
                    mldEpisodesList.postValue(true)
                    mldGetAnimeEpisodeData.postValue(position)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "${App.context.getString(R.string.get_data_failed)}\n${e.message}".showToast()
            }
        }
    }

    fun getPlayData(partUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                playModel.getPlayData(partUrl, animeEpisodeDataBean).apply {
                    playBeanDataList.clear()
                    episodesList.clear()
                    playBeanDataList.addAll(first)
                    episodesList.addAll(second)
                    playBean = third
                    mldPlayBean.postValue(playBean)
                    mldEpisodesList.postValue(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    // 更新追番集数数据
    fun updateFavoriteData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        lastEpisode: String,
        time: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favoriteAnimeDao = getAppDataBase().favoriteAnimeDao()
                val favoriteAnimeBean = favoriteAnimeDao.getFavoriteAnime(detailPartUrl)
                if (favoriteAnimeBean != null) {
                    favoriteAnimeBean.lastEpisode = lastEpisode
                    favoriteAnimeBean.lastEpisodeUrl = lastEpisodeUrl
                    favoriteAnimeBean.time = time
                    favoriteAnimeDao.updateFavoriteAnime(favoriteAnimeBean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 插入观看历史记录
    fun insertHistoryData(detailPartUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("更新播放历史", detailPartUrl)
            try {
                if (animeCover.isBlank()) {
                    "封面为空，无法记录播放历史".showToast()
                } else {
                    getAppDataBase().historyDao().insertHistory(
                        HistoryBean(
                            Constant.ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                            playBean?.title?.title ?: "",
                            System.currentTimeMillis(),
                            animeCover,
                            partUrl,
                            animeEpisodeDataBean.title
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}