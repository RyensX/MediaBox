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

/**
 * 获取播放界面数据的接口
 */
public interface IPlayModel extends IBase {
    String implName = "PlayModel";

    void setActivity(Activity activity);

    void clearActivity();

    /**
     * 获取此部番剧封面
     *
     * @param detailPartUrl 页面部分url，不为null
     * @param callback      回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                      若异步获取数据，则return null，使用callBack返回数据
     * @return ImageBean，可为null。番剧封面
     */
    @Nullable
    ImageBean getAnimeCoverImageBean(String detailPartUrl, @NonNull AnimeCoverImageBeanCallBack callback);

    /**
     * 获取播放页面相关数据
     *
     * @param partUrl              页面部分url，不为null
     * @param animeEpisodeDataBean 此集番剧数据，不为null，直接对此引用进行数据设置，不要更改此变量指向的对象
     * @param callback             回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                             若异步获取数据，则return null，使用callBack返回数据
     * @return Triple，可为null
     * ArrayList<IAnimeDetailBean>：播放页下方数据ArrayList，不为null；
     * ArrayList<AnimeEpisodeDataBean>：番剧集数列表，不为null；
     * PlayBean：此集番剧数据，不为null
     */
    @Nullable
    Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> getPlayData(
            @NonNull String partUrl,
            AnimeEpisodeDataBean animeEpisodeDataBean,
            @NonNull PlayDataCallBack callback
    );

    /**
     * 获取当前页面播放视频的地址
     *
     * @param partUrl  页面部分url，不为null
     * @param callback 回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                 若异步获取数据，则return null，使用callBack返回数据
     * @return String，可为null。此页面播放的视频地址
     */
    @Nullable
    String getAnimeEpisodeUrlData(@NonNull String partUrl, @NonNull AnimeEpisodeUrlDataCallBack callback);

    /**
     * 获取传入partUrl页面对应的视频的数据
     *
     * @param partUrl              页面部分url，不为null
     * @param animeEpisodeDataBean partUrl页面对应的视频的数据Bean，不为null，直接对此变量设置数据，不要更改此变量指向的对象
     * @param callback             回调接口，不为null。若同步获取数据，请直接return返回，忽略callBack；
     *                             若异步获取数据，则return null，使用callBack返回数据
     * @return Boolean，可为null。获取成功true，否则false
     */
    @Nullable
    Boolean refreshAnimeEpisodeData(@NonNull String partUrl, AnimeEpisodeDataBean animeEpisodeDataBean,
                                    @NonNull AnimeEpisodeDataCallBack callback);

    interface PlayDataCallBack {
        void onSuccess(@NonNull Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> t);

        void onError(@NonNull Exception e);
    }

    interface AnimeEpisodeDataCallBack {
        void onSuccess(boolean b);

        void onError(@NonNull Exception e);
    }

    interface AnimeEpisodeUrlDataCallBack {
        void onSuccess(@NonNull String url);

        void onError(@NonNull Exception e);
    }

    interface AnimeCoverImageBeanCallBack {
        void onSuccess(@NonNull ImageBean imageBean);

        void onError(@NonNull Exception e);
    }
}