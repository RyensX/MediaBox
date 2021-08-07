package com.skyd.imomoe.model.interfaces;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

/**
 * 界面跳转处理接口
 */
public interface IRouteProcessor {
    String implName = "RouteProcessor";

    /**
     * 处理根据actionUrl跳转
     *
     * @param context   Context
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    boolean process(Context context, String actionUrl);

    /**
     * 处理根据actionUrl跳转
     *
     * @param activity  Activity
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    boolean process(Activity activity, String actionUrl);

    /**
     * 处理根据actionUrl跳转
     *
     * @param fragment  Fragment
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    default boolean process(Fragment fragment, String actionUrl) {
        Activity activity = fragment.getActivity();
        if (activity != null) return process(activity, actionUrl);
        else return false;
    }
}