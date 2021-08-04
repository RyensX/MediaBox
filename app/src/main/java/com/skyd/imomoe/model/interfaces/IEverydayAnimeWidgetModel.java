package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;

import java.util.ArrayList;
import java.util.List;

public interface IEverydayAnimeWidgetModel extends IBaseModel {
    String implName = "EverydayAnimeWidgetModel";

    ArrayList<List<AnimeCoverBean>> getEverydayAnimeData();
}