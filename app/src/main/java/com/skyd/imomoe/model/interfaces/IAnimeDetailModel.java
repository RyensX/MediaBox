package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.IAnimeDetailBean;
import com.skyd.imomoe.bean.ImageBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;

public interface IAnimeDetailModel extends IBaseModel {
    String implName = "AnimeDetailModel";

    Triple<ImageBean, String, ArrayList<IAnimeDetailBean>> getAnimeDetailData(String partUrl);
}