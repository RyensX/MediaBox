package com.skyd.imomoe.model.interfaces;

import com.skyd.imomoe.bean.AnimeCoverBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取每日更新番剧桌面小组件的数据接口
 */
public interface IEverydayAnimeWidgetModel extends IBase {
    String implName = "EverydayAnimeWidgetModel";

    /**
     * 获取桌面小组件的每日番剧数据
     *
     * @return ArrayList，不可为null。每天更新番剧的ArrayList，其中共有七条
     */
    ArrayList<List<AnimeCoverBean>> getEverydayAnimeData();
}