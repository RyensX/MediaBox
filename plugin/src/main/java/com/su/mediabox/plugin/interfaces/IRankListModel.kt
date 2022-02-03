package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

/**
 * 获取排行榜界面Tab数据的接口
 */
interface IRankListModel : IBase {
    /**
     * 获取排行榜列表数据
     *
     * @param partUrl  页面部分url，不为null
     * @return Pair，不可为null
     * List<AnimeCoverBean>：排行榜列表数据List，不为null
     * PageNumberBean：下一页数据地址Bean，可为null，为空则没有下一页
     */
    suspend fun getRankListData(partUrl: String): Pair<List<AnimeCoverBean>, PageNumberBean?>

    companion object {
        const val implName = "RankListModel"
    }
}