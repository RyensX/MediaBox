package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.TabBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface IRankModel extends IBaseModel {
    String implName = "RankModel";

    Pair<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>> getRankData();
}