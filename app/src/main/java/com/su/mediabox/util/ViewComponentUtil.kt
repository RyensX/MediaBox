package com.su.mediabox.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.v2.viewmodel.VideoDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun bindHistoryPlayInfo(
    observer: Observer<HistoryBean?>
) {
    val context = AppRouteProcessor.currentActivity?.get() ?: return
    val detailUrl = (context as? ComponentActivity)?.let {
        when (it) {
            is VideoDetailActivity -> ViewModelProvider(it)[VideoDetailViewModel::class.java].partUrl
            else -> null
        }
    } ?: return
    context.lifecycleScope.launch(Dispatchers.Main) {
        //优先绑定收藏的历史播放记录
        withContext(Dispatchers.IO) {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailUrl)
        }?.also {
            getAppDataBase().favoriteAnimeDao()
                .getFavoriteAnimeLiveData(detailUrl).observe(context, observer)
            return@launch
        }
        getAppDataBase().historyDao()
            .getHistoryLiveData(detailUrl)
            .observe(context, observer)
    }
}