package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.TabBean;

import java.util.ArrayList;

/**
 * 获取排行榜界面每个Tab详细数据的接口
 */
public interface IRankModel extends IBase {
    String implName = "RankModel";

    /**
     * 获取排行榜Tab数据
     *
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return ArrayList，可为null。排行榜TabArrayList
     */
    @Nullable
    ArrayList<TabBean> getRankTabData(@NonNull RankTabDataCallBack callBack);

    interface RankTabDataCallBack {
        void onSuccess(@NonNull ArrayList<TabBean> list);

        void onError(@NonNull Exception e);
    }
}