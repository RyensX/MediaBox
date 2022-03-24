package com.su.mediabox.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.v2.viewmodel.VideoDetailViewModel

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
    //优先绑定收藏的历史播放记录
    val lv = getAppDataBase().favoriteAnimeDao()
        .getFavoriteAnimeLiveData(detailUrl)
    if (lv.value == null)
        getAppDataBase().historyDao()
            .getHistoryLiveData(detailUrl)
            .observe(context, observer)
    else
        lv.observe(context, observer)
}