package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

/**
 * 获取季度番剧数据的接口
 */
public interface IMonthAnimeModel extends IBase {
    String implName = "MonthAnimeModel";

    /**
     * @param partUrl  页面部分url，不为null
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Pair，可为null
     * ArrayList<AnimeCoverBean>：季度番剧数据ArrayList，不为null；
     * PageNumberBean：下一页数据地址，可为null，为空则没有下一页
     */
    @Nullable
    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getMonthAnimeData(
            @NonNull String partUrl,
            @NonNull AllTabDataCallBack callBack
    );

    interface AllTabDataCallBack {
        void onSuccess(@NonNull Pair<ArrayList<AnimeCoverBean>, PageNumberBean> p);

        void onError(@NonNull Exception e);
    }
}