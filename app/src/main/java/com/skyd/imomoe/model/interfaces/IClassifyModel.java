package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.ClassifyBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

public interface IClassifyModel extends IBaseModel {
    ArrayList<ClassifyBean> getClassifyTabData();

    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getClassifyData(String partUrl);
}