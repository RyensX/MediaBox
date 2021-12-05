package com.skyd.imomoe.model.impls.custom

object TestClass {
    val classMap =
        hashMapOf(
            "IAnimeDetailModel" to CustomAnimeDetailModel::class.java,
            "IAnimeShowModel" to CustomAnimeShowModel::class.java,
            "IClassifyModel" to CustomClassifyModel::class.java,
            "IEverydayAnimeModel" to CustomEverydayAnimeModel::class.java,
            "IHomeModel" to CustomHomeModel::class.java,
            "IMonthAnimeModel" to CustomMonthAnimeModel::class.java,
            "IPlayModel" to CustomPlayModel::class.java,
            "IRankModel" to CustomRankModel::class.java,
            "ISearchModel" to CustomSearchModel::class.java,
            "IConst" to CustomConst::class.java,
            "IUtil" to CustomUtil::class.java,
            "IRouteProcessor" to CustomRouteProcessor::class.java,
            "IRankListModel" to CustomRankListModel::class.java,
            "IEverydayAnimeWidgetModel" to CustomEverydayAnimeWidgetModel::class.java
        )
}