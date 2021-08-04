package com.skyd.imomoe.model.interfaces;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.bean.AnimeEpisodeDataBean;
import com.skyd.imomoe.bean.IAnimeDetailBean;
import com.skyd.imomoe.bean.ImageBean;
import com.skyd.imomoe.bean.PlayBean;
import com.skyd.imomoe.model.util.Triple;

import java.util.ArrayList;

public interface IPlayModel extends IBaseModel {
    String implName = "PlayModel";

    void setActivity(Activity activity);

    void clearActivity();

    ImageBean getAnimeCoverImageBean(String detailPartUrl);

    @Nullable
    Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> getPlayData(
            String partUrl,
            AnimeEpisodeDataBean animeEpisodeDataBean,
            @Nullable OnPlayDataCallBack callback
    );

    @Nullable
    String getAnimeEpisodeUrlData(String partUrl, @Nullable OnEpisodeUrlDataCallBack callback);

    @Nullable
    Boolean refreshAnimeEpisodeData(String partUrl, AnimeEpisodeDataBean animeEpisodeDataBean,
                                    @Nullable OnEpisodeDataCallBack callback);

    interface OnPlayDataCallBack {
        void onSuccess(@NonNull Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> t);

        void onError(@NonNull Exception e);
    }

    interface OnEpisodeDataCallBack {
        void onSuccess(boolean b);

        void onError(@NonNull Exception e);
    }

    interface OnEpisodeUrlDataCallBack {
        void onSuccess(@NonNull String url);

        void onError(@NonNull Exception e);
    }
}