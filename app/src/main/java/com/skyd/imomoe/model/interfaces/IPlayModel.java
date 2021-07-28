package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeEpisodeDataBean;
import com.skyd.imomoe.bean.IAnimeDetailBean;
import com.skyd.imomoe.bean.ImageBean;
import com.skyd.imomoe.bean.PlayBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;

public interface IPlayModel extends IBaseModel {
    ImageBean getAnimeCoverImageBean(String detailPartUrl);

    Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> getPlayData(
            String partUrl,
            AnimeEpisodeDataBean animeEpisodeDataBean
    );

    String getAnimeEpisodeUrlData(String partUrl);

    boolean refreshAnimeEpisodeData(String partUrl, AnimeEpisodeDataBean animeEpisodeDataBean);
}