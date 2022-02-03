package com.su.mediabox.plugin.interfaces

import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

/**
 * 获取搜索界面数据的接口
 */
interface ISearchModel : IBase {
    /**
     * 获取搜索结果数据
     *
     * @param keyWord  搜索关键词，不为null
     * @param partUrl  搜索页面部分url，不为null
     * @return Pair，不可为null
     * ArrayList<AnimeCoverBean>：搜索结果ArrayList，不为null
     * PageNumberBean：下一页数据地址Bean，可为null
     */
    suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?>

    companion object {
        const val implName = "SearchModel"
    }
}