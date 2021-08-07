package com.skyd.imomoe.model.interfaces;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.ClassifyBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

/**
 * 获取分类界面数据的接口
 */
public interface IClassifyModel extends IBase {
    String implName = "ClassifyModel";

    void setActivity(Activity activity);

    void clearActivity();

    /**
     * 获取分类界面头部分类信息
     *
     * @param callback 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return ArrayList，可为null。分类界面头部分类数据List
     */
    @Nullable
    ArrayList<ClassifyBean> getClassifyTabData(@NonNull ClassifyTabDataCallBack callback);

    /**
     * 获取每个分类下的内容
     *
     * @param partUrl  页面部分url
     * @param callBack 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return Pair，可为null
     * ArrayList<AnimeCoverBean>：数据List，不可为null；
     * PageNumberBean：下一页地址数据，可为null
     */
    @Nullable
    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getClassifyData(
            @NonNull String partUrl,
            @NonNull ClassifyDataCallBack callBack
    );

    interface ClassifyTabDataCallBack {
        void onSuccess(@NonNull ArrayList<ClassifyBean> list);

        void onError(@NonNull Exception e);
    }

    interface ClassifyDataCallBack {
        void onSuccess(@NonNull Pair<ArrayList<AnimeCoverBean>, PageNumberBean> p);

        void onError(@NonNull Exception e);
    }
}