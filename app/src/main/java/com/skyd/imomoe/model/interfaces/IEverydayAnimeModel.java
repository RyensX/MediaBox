package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.AnimeShowBean;
import com.skyd.imomoe.bean.TabBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;
import java.util.List;

public interface IEverydayAnimeModel extends IBaseModel {
    String implName = "EverydayAnimeModel";

    Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, AnimeShowBean> getEverydayAnimeData();
}