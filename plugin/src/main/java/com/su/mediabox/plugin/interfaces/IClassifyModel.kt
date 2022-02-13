package com.su.mediabox.plugin.interfaces

import android.app.Activity
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.ClassifyBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

/**
 * 获取分类界面数据的接口
 */
interface IClassifyModel : IBase {
    /**
     * 获取分类界面头部分类信息
     *
     * @return ArrayList，不可为null。分类界面头部分类数据List
     */
    suspend fun getClassifyTabData(): ArrayList<ClassifyBean>

    /**
     * 获取每个分类下的内容
     *
     * @param partUrl  页面部分url
     * @return Pair，不可为null
     * ArrayList<AnimeCoverBean>：数据List，不可为null；
     * PageNumberBean：下一页地址数据，可为null
     */
    suspend fun getClassifyData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?>

    companion object {
        const val implName = "ClassifyModel"
    }
}