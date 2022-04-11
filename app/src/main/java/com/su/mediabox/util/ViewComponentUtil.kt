package com.su.mediabox.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.EpisodeData
import com.su.mediabox.v2.view.activity.VideoDetailActivity
import com.su.mediabox.v2.viewmodel.VideoDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

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

/**
 * 从字符串中提取第一个数字，只支持阿拉伯数字
 *
 * 没有任何数字的优先放到后面
 */
fun String.getEpisodeNum(): Int {
    var isNum = false
    var num = 1
    var hasNum = false
    var nd = 1
    for (c in toCharArray().reversed())
        if (c.isDigit()) {
            isNum = true
            num += c.digitToInt() * nd
            nd *= 10
            hasNum = true
        } else if (isNum)
            break
    if (!hasNum)
        num += length * 100
    if (lowercase(Locale.getDefault()).contains("pv"))
        num -= length * 100
    return num
}

/**
 * 计算正确顺序的剧集列表
 */
fun getCorrectEpisodeList(list: List<EpisodeData>): List<EpisodeData> {
    var result = list
    if (result.isEmpty())
        return result
    val size = list.size
    //检查前后两端大小，只要有一项符合即可，尊重原数据顺序
    if (!(list[0].name.getEpisodeNum() < list[1].name.getEpisodeNum() ||
                list[size - 2].name.getEpisodeNum() < list[size - 1].name.getEpisodeNum())
    )
        result = list.asReversed()

    return result
}