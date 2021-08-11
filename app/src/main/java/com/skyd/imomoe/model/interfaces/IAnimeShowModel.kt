package com.skyd.imomoe.model.interfaces

import com.skyd.imomoe.bean.IAnimeShowBean
import com.skyd.imomoe.bean.PageNumberBean

/**
 * 获取首页每个Tab下方内容的数据接口
 */
interface IAnimeShowModel : IBase {
    /**
     * 获取首页某一个Tab下的内容
     *
     * @param partUrl  页面部分url，不为null
     * @return Pair，不可为null
     * ArrayList<IAnimeShowBean>：数据List，不可为null；
     * PageNumberBean：下一页数据地址Bean，可为null，为空则没有下一页
     */
    suspend fun getAnimeShowData(partUrl: String): Pair<ArrayList<IAnimeShowBean>, PageNumberBean?>

    companion object {
        const val implName = "AnimeShowModel"
    }
}