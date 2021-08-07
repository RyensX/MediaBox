package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.List;

/**
 * 获取排行榜界面Tab数据的接口
 */
public interface IRankListModel extends IBase {
    String implName = "RankListModel";

    /**
     * 获取排行榜列表数据
     *
     * @param partUrl  页面部分url，不为null
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Pair，可为null
     * List<AnimeCoverBean>：排行榜列表数据List，不为null
     * PageNumberBean：下一页数据地址Bean，可为null，为空则没有下一页
     */
    @Nullable
    Pair<List<AnimeCoverBean>, PageNumberBean> getRankListData(
            @NonNull String partUrl,
            @NonNull RankListDataCallBack callBack
    );

    interface RankListDataCallBack {
        void onSuccess(@NonNull Pair<List<AnimeCoverBean>, PageNumberBean> p);

        void onError(@NonNull Exception e);
    }
}