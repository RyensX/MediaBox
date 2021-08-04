package com.skyd.imomoe.model.interfaces;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

public interface IRouterProcessor {
    String implName = "RouterProcessor";

    /**
     * 处理根据网址跳转
     * @param context Context
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    boolean process(Context context, String actionUrl);

    /**
     * 处理根据网址跳转
     * @param activity Activity
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    boolean process(Activity activity, String actionUrl);

    /**
     * 处理根据网址跳转
     * @param fragment Fragment
     * @param actionUrl 跳转路径
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    default boolean process(Fragment fragment, String actionUrl) {
        Activity activity = fragment.getActivity();
        if (activity != null) return process(activity, actionUrl);
        else return false;
    }
}