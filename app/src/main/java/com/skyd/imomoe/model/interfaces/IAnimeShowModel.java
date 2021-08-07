package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.IAnimeShowBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

/**
 * 获取首页每个Tab下方内容的数据接口
 */
public interface IAnimeShowModel extends IBase {
    String implName = "AnimeShowModel";

    /**
     * 获取首页某一个Tab下的内容
     *
     * @param partUrl  页面部分url，不为null
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Pair，可为null
     * ArrayList<IAnimeShowBean>：数据List，不可为null；
     * PageNumberBean：下一页数据地址Bean，可为null，为空则没有下一页
     */
    @Nullable
    Pair<ArrayList<IAnimeShowBean>, PageNumberBean> getAnimeShowData(
            @NonNull String partUrl,
            @NonNull AnimeShowDataCallBack callBack
    );

    interface AnimeShowDataCallBack {
        void onSuccess(@NonNull Pair<ArrayList<IAnimeShowBean>, PageNumberBean> p);

        void onError(@NonNull Exception e);
    }
}