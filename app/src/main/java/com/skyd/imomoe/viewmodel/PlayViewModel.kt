package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.PlayModel
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.model.util.Triple
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.*


class PlayViewModel : ViewModel() {
    private val playModel: IPlayModel by lazy {
        DataSourceManager.create(IPlayModel::class.java) ?: PlayModel()
    }
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

    fun setActivity(activity: Activity) {
        playModel.setActivity(activity)
    }

    fun clearActivity() {
        playModel.clearActivity()
    }

    fun refreshAnimeEpisodeData(partUrl: String, currentEpisodeIndex: Int, title: String = "") {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                this@PlayViewModel.partUrl = partUrl
                playModel.refreshAnimeEpisodeData(partUrl, animeEpisodeDataBean,
                    object :
                        IPlayModel.AnimeEpisodeDataCallBack {
                        override fun onSuccess(b: Boolean) {
                            if (b) {
                                animeEpisodeDataBean.title = title
                                mldAnimeEpisodeDataRefreshed.postValue(true)
                            } else {
                                throw RuntimeException("html play class not found")
                            }
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                            animeEpisodeDataBean.actionUrl = "animeEpisode1"
                            animeEpisodeDataBean.title = ""
                            animeEpisodeDataBean.videoUrl = ""
                            mldAnimeEpisodeDataRefreshed.postValue(false)
                            (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
                        }
                    }).apply {
                    if (this == true) {
                        animeEpisodeDataBean.title = title
                        mldAnimeEpisodeDataRefreshed.postValue(true)
                    } else if (this == false) {
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
                playModel.getAnimeEpisodeUrlData(partUrl,
                    object :
                        IPlayModel.AnimeEpisodeUrlDataCallBack {
                        override fun onSuccess(url: String) {
                            episodesList[position].videoUrl = url
                            mldEpisodesList.postValue(true)
                            mldGetAnimeEpisodeData.postValue(position)
                        }

                        override fun onError(e: Exception) {
                            throw e
                        }

                    }).apply {
//                    this ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
                    if (this != null) {
                        episodesList[position].videoUrl = this
                        mldEpisodesList.postValue(true)
                        mldGetAnimeEpisodeData.postValue(position)
                    }
                }
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
                playModel.getPlayData(partUrl, animeEpisodeDataBean,
                    object :
                        IPlayModel.PlayDataCallBack {
                        override fun onSuccess(t: Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean>) {
                            t.apply {
                                playBeanDataList.clear()
                                episodesList.clear()
                                playBeanDataList.addAll(first)
                                episodesList.addAll(second)
                                playBean = third
                                mldPlayBean.postValue(playBean)
                                mldEpisodesList.postValue(true)
                            }
                        }

                        override fun onError(e: Exception) {
                            throw e
                        }
                    }
                ).apply {
                    if (this == null) return@apply
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
                if (animeCover.url.isBlank()) {
                    playModel.getAnimeCoverImageBean(detailPartUrl, object :
                        IPlayModel.AnimeCoverImageBeanCallBack {
                        override fun onSuccess(imageBean: ImageBean) {
                            getAppDataBase().historyDao().insertHistory(
                                HistoryBean(
                                    ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                                    playBean?.title?.title ?: "",
                                    System.currentTimeMillis(),
                                    imageBean,
                                    partUrl,
                                    animeEpisodeDataBean.title
                                )
                            )
                        }

                        override fun onError(e: java.lang.Exception) {
                            e.printStackTrace()
                        }

                    }).apply {
                        this ?: return@apply
                        getAppDataBase().historyDao().insertHistory(
                            HistoryBean(
                                ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                                playBean?.title?.title ?: "",
                                System.currentTimeMillis(),
                                this,
                                partUrl,
                                animeEpisodeDataBean.title
                            )
                        )
                    }
                } else {
                    getAppDataBase().historyDao().insertHistory(
                        HistoryBean(
                            ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
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

    fun getAnimeCoverImageBean(detailPartUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                playModel.getAnimeCoverImageBean(detailPartUrl, object :
                    IPlayModel.AnimeCoverImageBeanCallBack {
                    override fun onSuccess(imageBean: ImageBean) {
                        animeCover.url = imageBean.url
                        animeCover.referer = imageBean.referer
                        mldAnimeCover.postValue(true)
                    }

                    override fun onError(e: java.lang.Exception) {
                        mldAnimeCover.postValue(false)
                        e.printStackTrace()
                        ("${App.context.getString(R.string.get_data_failed)}\n${e.message}").showToastOnThread()
                    }

                }).apply {
                    this ?: return@apply
                    animeCover.url = url
                    animeCover.referer = referer
                    mldAnimeCover.postValue(true)
                }
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