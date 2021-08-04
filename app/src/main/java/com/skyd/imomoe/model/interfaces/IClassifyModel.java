package com.skyd.imomoe.model.interfaces;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.ClassifyBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

public interface IClassifyModel extends IBaseModel {
    String implName = "ClassifyModel";

    void setActivity(Activity activity);

    void clearActivity();

    @Nullable
    ArrayList<ClassifyBean> getClassifyTabData(@Nullable OnClassifyTabDataCallBack callback);

    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getClassifyData(String partUrl);

    interface OnClassifyTabDataCallBack {
        void onSuccess(@NonNull ArrayList<ClassifyBean> list);

        void onError(@NonNull Exception e);
    }
}