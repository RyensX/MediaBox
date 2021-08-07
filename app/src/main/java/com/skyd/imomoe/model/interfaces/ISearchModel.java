package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

/**
 * 获取搜索界面数据的接口
 */
public interface ISearchModel extends IBase {
    String implName = "SearchModel";

    /**
     * 获取搜索结果数据
     *
     * @param keyWord  搜索关键词，不为null
     * @param partUrl  搜索页面部分url，不为null
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Pair，可为null
     * ArrayList<AnimeCoverBean>：搜索结果ArrayList，不为null
     * PageNumberBean：下一页数据地址Bean，不为null
     */
    @Nullable
    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getSearchData(
            @NonNull String keyWord,
            @NonNull String partUrl,
            @NonNull SearchDataCallBack callBack
    );

    interface SearchDataCallBack {
        void onSuccess(@NonNull Pair<ArrayList<AnimeCoverBean>, PageNumberBean> p);

        void onError(@NonNull Exception e);
    }
}