package com.su.mediabox.plugin

import com.su.mediabox.customdatasourcesample1.*
import com.su.mediabox.plugin.interfaces.*

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class ComponentFactory : IComponentFactory() {

    override fun <T : IBase> create(clazz: Class<T>) = when (clazz) {
        IAnimeDetailModel::class.java -> CustomAnimeDetailModel()
        IMonthAnimeModel::class.java -> CustomMonthAnimeModel()
        IAnimeShowModel::class.java -> CustomAnimeShowModel()
        IClassifyModel::class.java -> CustomClassifyModel()
        IConst::class.java -> CustomConst
        IEverydayAnimeModel::class.java -> CustomEverydayAnimeModel()
        IEverydayAnimeWidgetModel::class.java -> CustomEverydayAnimeWidgetModel()
        IHomeModel::class.java -> CustomHomeModel()
        IHomeModel::class.java -> CustomHomeModel()
        IPlayModel::class.java -> CustomPlayModel()
        IRankListModel::class.java -> CustomRankListModel()
        IRankModel::class.java -> CustomRankModel()
        IRouteProcessor::class.java -> CustomRouteProcessor()
        ISearchModel::class.java -> CustomSearchModel()
        IUtil::class.java -> CustomUtil
        else -> null
    } as? T

}