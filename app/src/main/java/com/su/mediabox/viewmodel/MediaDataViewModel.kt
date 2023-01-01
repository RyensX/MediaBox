package com.su.mediabox.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.plugin.MediaUpdateCheck
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent
import com.su.mediabox.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MediaDataViewModel : ViewModel() {

    private var filterNameFlow = MutableStateFlow("")
    var filterCount = 0
        private set
    private val favoriteFlow = getAppDataBase().favoriteDao().getFavoriteListLiveData().asFlow()

    //同时关注数据库数据和过滤名称
    val favorite = favoriteFlow.combine(filterNameFlow) { data, filter ->
        (if (filter.isNotBlank())
            data.filter { it.mediaTitle.contains(filter) }
        else
            data).also {
            //统计
            filterCount = data.size - it.size
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mutableListOf())

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
            _updateCount.value = 0
        }

    val update = getOfflineDatabase().mediaUpdateDao().getAllUpdateRecordFlow()

    private val _updateCount = MutableStateFlow(0)
    val updateCount = _updateCount.asLiveData()

    @FlowPreview
    fun checkMediaUpdate() {
        mediaUpdateDataComponent ?: return
        if (_updateCount.value != 0) return

        favorite.value.also {
            PluginManager.currentLaunchPlugin.value?.also { plugin ->
                viewModelScope.launch(updateDispatcher) {
                    MediaUpdateCheck.checkMediaUpdate(it, plugin, mediaUpdateDataComponent, {
                        _updateCount.apply { value += 1 }
                    }, {
                        _updateCount.apply { value -= 1 }
                    }) {
                        if (it.isNotEmpty())
                            launch(Dispatchers.Main) {
                                App.context.getString(R.string.media_update_toast, it.size)
                                    .showToast(Toast.LENGTH_LONG)
                            }
                        _updateCount.value = 0
                    }
                }
            }
        }
    }

    fun filter(name: String?) {
        name?.also {
            val nameTrim = it.trim()
            if (nameTrim != filterNameFlow.value) {
                filterNameFlow.value = nameTrim
            }
        }
    }
}