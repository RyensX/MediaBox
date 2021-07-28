package com.skyd.imomoe.model.interfaces;

import android.util.Pair;

import com.skyd.imomoe.bean.IAnimeShowBean;
import com.skyd.imomoe.bean.PageNumberBean;

import java.util.ArrayList;

public interface IAnimeShowModel extends IBaseModel {
    Pair<ArrayList<IAnimeShowBean>, PageNumberBean> getAnimeShowData(String partUrl);
}