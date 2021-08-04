package com.skyd.imomoe.model.interfaces;


import androidx.annotation.NonNull;

public interface IUtil {
    String implName = "Util";

    /**
     * 通过播放页面的网址获取详情页面的网址
     *
     * @param episodeUrl 播放页面的网址
     * @return 详情页面的网址
     */
    String getDetailLinkByEpisodeLink(@NonNull String episodeUrl);
}