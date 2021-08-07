package com.skyd.imomoe.model.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 获取MAIN_URL、页面跳转信息、自定义数据jar包的关于信息等
 */
public interface IConst extends IBase {
    String implName = "Const";

    /**
     * @return MAIN_URL
     */
    @Nullable
    String MAIN_URL();

    /**
     * @return jar包的关于信息
     */
    @NonNull
    default String about() {
        return MAIN_URL() + "";
    }

    /**
     * @return 跳转类实例
     */
    IActionUrl getActionUrl();

    interface IActionUrl {
        /**
         * @return 番剧详情界面跳转URL
         */
        String ANIME_DETAIL();

        /**
         * @return 播放界面跳转URL
         */
        String ANIME_PLAY();

        /**
         * @return 搜索界面跳转URL
         */
        String ANIME_SEARCH();

        /**
         * @return 排行榜界面跳转URL
         */
        String ANIME_RANK();
    }
}