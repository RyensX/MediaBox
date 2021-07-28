package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;

import java.util.ArrayList;

public interface IMonthAnimeModel extends IBaseModel {
    ArrayList<AnimeCoverBean> getMonthAnimeData(String partUrl);
}