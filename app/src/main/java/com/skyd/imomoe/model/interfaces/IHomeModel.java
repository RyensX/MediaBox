package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.TabBean;

import java.util.ArrayList;

public interface IHomeModel extends IBaseModel {
    String implName = "HomeModel";

    ArrayList<TabBean> getAllTabData();
}