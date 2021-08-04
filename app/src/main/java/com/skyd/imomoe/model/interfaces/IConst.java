package com.skyd.imomoe.model.interfaces;

import androidx.annotation.Nullable;

public interface IConst {
    String implName = "Const";

    @Nullable String MAIN_URL();

    IActionUrl getActionUrl();

    interface IActionUrl {
        String ANIME_DETAIL();

        String ANIME_PLAY();

        String ANIME_SEARCH();

        String ANIME_TOP();
    }
}