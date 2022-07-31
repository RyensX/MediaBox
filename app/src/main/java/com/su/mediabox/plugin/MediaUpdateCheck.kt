package com.su.mediabox.plugin

import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginManager.acquireComponent
import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent
import com.su.mediabox.util.Util
import com.su.mediabox.util.logD
import com.su.mediabox.util.logI
import com.su.mediabox.util.logW
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object MediaUpdateCheck {

    @FlowPreview
    suspend fun checkMediaUpdate(
        favorites: List<MediaFavorite>,
        pluginInfo: PluginInfo,
        mediaUpdateDataComponent: IMediaUpdateDataComponent,
        onMediaEmit: (() -> Unit)? = null,
        onMediaUpdated: (() -> Unit)? = null,
        onMediaUpdateCheckDone: (suspend (List<MediaUpdateRecord?>) -> Unit)? = null
    ) {
        val TAG = "媒体检查更新(${pluginInfo.name})"
        val mediaDao = pluginInfo.getAppDataBase().favoriteDao()
        val updateDao = pluginInfo.getOfflineDatabase().mediaUpdateDao()

        logD(TAG, "开始检查更新 收藏数:${favorites.size}")
        flow {
            favorites.forEach {
                val check =
                    Util.withoutExceptionGet { mediaUpdateDataComponent.enableUpdateCheck(it.updateTag) }
                        ?: false
                logD(
                    TAG,
                    "${it.mediaTitle}(${it.mediaUrl}) -> updateTag=${it.updateTag} 是否检查:$check"
                )
                if (check) {
                    //_updateCount.apply { value += 1 }
                    onMediaEmit?.invoke()
                    emit(it)
                }
            }
        }.flatMapMerge { media ->
            //并发检查
            flow {
                val record = Util.withoutExceptionGet {
                    //每个媒体检查更新限制30s
                    withTimeoutOrNull(30 * 1000) {
                        mediaUpdateDataComponent.getUpdateTag(media.mediaUrl)?.let {
                            //_updateCount.apply { value -= 1 }
                            onMediaUpdated?.invoke()
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
                                } else if (it != media.updateTag) {
                                    logI(
                                        TAG,
                                        "发现更新 ${media.mediaTitle}(${media.mediaUrl}) ${media.updateTag} -> $it"
                                    )
                                    MediaUpdateRecord(
                                        System.currentTimeMillis(),
                                        media.mediaUrl, media.mediaTitle,
                                        media.updateTag, it
                                    )
                                } else null

                            } else null
                        }
                    }

                }
                if (record == null)
                    logW(TAG, "target=${media.mediaTitle}(${media.mediaUrl}) 无更新")
                emit(record)
            }
        }
            .filter { it != null }
            .catch {
                it.printStackTrace()
            }
            .toList().also { data ->
                logD(TAG, "更新${data.size}条记录")
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

                // if (data.isNotEmpty())
                //     launch(Dispatchers.Main) {
                //         App.context.getString(R.string.media_update_toast, data.size)
                //             .showToast(Toast.LENGTH_LONG)
                //     }

                //_updateCount.value = 0
                onMediaUpdateCheckDone?.invoke(data)
            }
    }
}