package com.skyd.imomoe.model.interfaces;


import androidx.annotation.NonNull;

/**
 * 工具类
 */
public interface IUtil extends IBase {
    String implName = "Util";

    /**
     * 通过播放页面的网址获取详情页面的网址
     *
     * @param episodeUrl 播放页面的网址，不为null
     * @return 详情页面的网址，不可为null
     */
    String getDetailLinkByEpisodeLink(@NonNull String episodeUrl);
}