package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.TabBean;

import java.util.ArrayList;

/**
 * 获取首页数据的接口
 */
public interface IHomeModel extends IBase {
    String implName = "HomeModel";

    /**
     * 获取首页上方Tab数据
     *
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return ArrayList，可空。所有Tab的ArrayList
     */
    @Nullable
    ArrayList<TabBean> getAllTabData(@NonNull AllTabDataCallBack callBack);

    interface AllTabDataCallBack {
        void onSuccess(@NonNull ArrayList<TabBean> list);

        void onError(@NonNull Exception e);
    }
}