package com.skyd.imomoe.model.interfaces

import android.app.Activity
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean

/**
 * 获取分类界面数据的接口
 */
interface IClassifyModel : IBase {
    @Deprecated("This method will cause a memory leak!!!")
    fun setActivity(activity: Activity)

    fun clearActivity()

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