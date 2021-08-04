package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.TabBean;

import java.util.ArrayList;

public interface IRankModel extends IBaseModel {
    String implName = "RankModel";

    ArrayList<TabBean> getRankTabData();
}