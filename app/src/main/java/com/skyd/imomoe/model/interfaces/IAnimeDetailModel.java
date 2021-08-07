package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.IAnimeDetailBean;
import com.skyd.imomoe.bean.ImageBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;

/**
 * 获取番剧详情数据接口
 */
public interface IAnimeDetailModel extends IBase {
    String implName = "AnimeDetailModel";

    /**
     * 获取番剧详情页数据
     *
     * @param partUrl  页面部分url，不为null
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Triple，可为null
     * ImageBean：番剧封面图片类，不可为null；
     * String：番剧名，不可为null；
     * ArrayList<IAnimeDetailBean>：详情页数据List，不可为null
     */
    @Nullable
    Triple<ImageBean, String, ArrayList<IAnimeDetailBean>> getAnimeDetailData(
            @NonNull String partUrl,
            @NonNull AnimeDetailDataCallBack callBack
    );

    interface AnimeDetailDataCallBack {
        void onSuccess(@NonNull Triple<ImageBean, String, ArrayList<IAnimeDetailBean>> t);

        void onError(@NonNull Exception e);
    }
}