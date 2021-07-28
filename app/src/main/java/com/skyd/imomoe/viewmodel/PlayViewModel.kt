package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.model.impls.PlayModel
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.*


class PlayViewModel : ViewModel() {
    private val playModel: IPlayModel = PlayModel()
    var playBean: PlayBean? = null
    var partUrl: String = ""
    var animeCover: ImageBean = ImageBean("", "", "", "")
    var mldAnimeCover: MutableLiveData<Boolean> = MutableLiveData()
    var mldPlayBean: MutableLiveData<PlayBean> = MutableLiveData()
    var playBeanDataList: MutableList<IAnimeDetailBean> = ArrayList()
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    var currentEpisodeIndex = 0
    val mldEpisodesList: MutableLiveData<Boolean> = MutableLiveData()
    val animeEpisodeDataBean = AnimeEpisodeDataBean("animeEpisode1", "", "")
    val mldAnimeEpisodeDataRefreshed: MutableLiveData<Boolean> = MutableLiveData()
    val mldGetAnimeEpisodeData: MutableLiveData<Int> = MutableLiveData()

    fun refreshAnimeEpisodeData(partUrl: String, currentEpisodeIndex: Int, title: String = "") {
        GlobalScope.launch(Dispatchers.IO) {
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
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
            this@PlayViewModel.currentEpisodeIndex = currentEpisodeIndex
        }
    }

    fun getAnimeEpisodeUrlData(partUrl: String, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
//                this@PlayViewModel.partUrl = partUrl
                playModel.getAnimeEpisodeUrlData(partUrl).apply {
                    this ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
                    episodesList[position].videoUrl = this
                }
                mldEpisodesList.postValue(true)
                mldGetAnimeEpisodeData.postValue(position)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    fun getPlayData(partUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                playModel.getPlayData(partUrl, animeEpisodeDataBean).apply {
                    playBeanDataList.clear()
                    episodesList.clear()
                    playBeanDataList.addAll(first)
                    episodesList.addAll(second)
                    playBean = third
                }
                mldPlayBean.postValue(playBean)
                mldEpisodesList.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
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
        GlobalScope.launch(Dispatchers.IO) {
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
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val cover = if (animeCover.url.isBlank()) {
                    playModel.getAnimeCoverImageBean(detailPartUrl) ?: return@launch
                } else animeCover
                getAppDataBase().historyDao().insertHistory(
                    HistoryBean(
                        ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                        playBean?.title?.title ?: "",
                        System.currentTimeMillis(),
                        cover,
                        partUrl,
                        animeEpisodeDataBean.title
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAnimeCoverImageBean(detailPartUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bean = playModel.getAnimeCoverImageBean(detailPartUrl)
                    ?: throw Exception("null object, 无法获取CoverImageBean")
                animeCover.url = bean.url
                animeCover.referer = bean.referer
                mldAnimeCover.postValue(true)
            } catch (e: Exception) {
                mldAnimeCover.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "PlayViewModel"
    }
}