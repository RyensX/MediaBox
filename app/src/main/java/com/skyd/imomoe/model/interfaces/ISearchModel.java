package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;
import com.skyd.imomoe.bean.PageNumberBean;
import com.skyd.imomoe.model.util.Pair;

import java.util.ArrayList;

public interface ISearchModel extends IBaseModel {
    String implName = "SearchModel";

    Pair<ArrayList<AnimeCoverBean>, PageNumberBean> getSearchData(String keyWord, String partUrl);
}