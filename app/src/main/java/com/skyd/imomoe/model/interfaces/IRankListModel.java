package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.List;

public interface IRankListModel extends IBaseModel {
    String implName = "RankListModel";

    Pair<List<AnimeCoverBean>, PageNumberBean> getRankListData(@NonNull String partUrl);
}