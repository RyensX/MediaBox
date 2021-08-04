package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

public interface IMonthAnimeModel extends IBaseModel {
    String implName = "MonthAnimeModel";

    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getMonthAnimeData(String partUrl);
}