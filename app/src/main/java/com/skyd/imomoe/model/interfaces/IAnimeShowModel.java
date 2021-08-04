package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.IAnimeShowBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

public interface IAnimeShowModel extends IBaseModel {
    String implName = "AnimeShowModel";

    Pair<ArrayList<IAnimeShowBean>, PageNumberBean> getAnimeShowData(String partUrl);
}