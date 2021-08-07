package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.AnimeShowBean;
import com.skyd.imomoe.bean.TabBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取每日更新番剧界面数据接口
 */
public interface IEverydayAnimeModel extends IBase {
    String implName = "EverydayAnimeModel";

    /**
     * 获取每日更新动漫数据
     *
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Triple，可为null
     * ArrayList<TabBean>：Tab信息ArrayList，不可为null；
     * ArrayList<List<AnimeCoverBean>>：每个Tab内容的ArrayList，不可为null；
     * AnimeShowBean：标题，例如：日更动漫，不可为null
     */
    @Nullable
    Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, AnimeShowBean> getEverydayAnimeData(
            @NonNull EverydayAnimeCallBack callBack
    );

    interface EverydayAnimeCallBack {
        void onSuccess(@NonNull Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, AnimeShowBean> t);

        void onError(@NonNull Exception e);
    }

}