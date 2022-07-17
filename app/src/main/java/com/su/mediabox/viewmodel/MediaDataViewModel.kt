package com.su.mediabox.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent
import com.su.mediabox.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MediaDataViewModel : ViewModel() {

    private val TAG = "媒体检查更新"

    val favorite = getAppDataBase().favoriteDao().getFavoriteListLiveData()
    val history = getAppDataBase().historyDao().getHistoryListLiveData()

    val mediaUpdateDataComponent =
        Util.withoutExceptionGet { PluginManager.acquireComponent<IMediaUpdateDataComponent>() }
    //Test
    //?:
    //object :IMediaUpdateDataComponent{
    //    override suspend fun getUpdateTag(detailUrl: String): String = getDataFormat("H:m").format(System.currentTimeMillis())
    //}

    private val updateDispatcher =
        Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            _updateState.value = false
        }

    val update = getOfflineDatabase().mediaUpdateDao().getAllUpdateRecordFlow()
    private val _updateState = MutableLiveData(mediaUpdateDataComponent != null)
    val updateState = _updateState.toLiveData()


    fun checkMediaUpdate() {
        mediaUpdateDataComponent ?: return
        viewModelScope.launch(updateDispatcher) {
            val mediaDao = getAppDataBase().favoriteDao()
            val updateDao = getOfflineDatabase().mediaUpdateDao()
            logD(TAG, "开始检查更新 收藏数:${favorite.value?.size ?: -1}")
            flow {
                favorite.value?.forEach {
                    val check =
                        Util.withoutExceptionGet { mediaUpdateDataComponent.enableUpdateCheck(it.updateTag) }
                            ?: false
                    logD(
                        TAG,
                        "${it.mediaTitle}(${it.mediaUrl}) -> updateTag=${it.updateTag} 是否检查:$check"
                    )
                    if (check) {
                        emit(it)
                    }
                }
            }.flatMapConcat { media ->
                //并发检查
                flow {
                    emit(Util.withoutExceptionGet {
                        mediaUpdateDataComponent.getUpdateTag(media.mediaUrl)?.let {
                            if (it.isNotBlank()) {
                                logD(
                                    TAG,
                                    "成功获取更新 target=${media.mediaTitle}(${media.mediaUrl}) oldUpdateTag=${media.updateTag} newUpdateTag=${it}"
                                )
                                if (media.updateTag.isNullOrEmpty()) {
                                    //没有初始化过有效更新标志则只更新标志
                                    mediaDao.updateFavorite(media.apply {
                                        updateTag = it
                                    })
                                    null
                                } else if (it != media.updateTag)
                                    MediaUpdateRecord(
                                        System.currentTimeMillis(),
                                        media.mediaUrl, media.mediaTitle,
                                        media.updateTag, it
                                    )
                                else null

                            } else null
                        }
                    })
                }
            }
                .filter { it != null }
                .catch {
                    it.printStackTrace()
                }
                .toList().also { data ->
                    logD(TAG, "更新${data.size}条记录")
                    _updateState.postValue(false)
                    //更新标志
                    runCatching {
                        val map = mutableMapOf<String, String>()
                        data.forEach {
                            if (it != null)
                                map[it.targetMedia] = it.newTag
                        }
                        mediaDao.updateFavoriteUpdateTags(map)
                    }.onFailure { it.printStackTrace() }
                    //插入更新记录
                    runCatching { updateDao.insert(*data.toTypedArray()) }.onFailure { it.printStackTrace() }

                    launch(Dispatchers.Main) {
                        App.context.getString(R.string.media_update_toast, data.size)
                            .showToast(Toast.LENGTH_LONG)
                    }
                }
        }
    }
}