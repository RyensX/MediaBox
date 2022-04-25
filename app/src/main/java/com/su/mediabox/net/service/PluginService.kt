package com.su.mediabox.net.service

import com.su.mediabox.bean.PreviewPluginInfo
import retrofit2.http.GET
import retrofit2.http.Url

interface PluginService {
    /**
     * 拉取插件预览信息，用于在线安装
     */
    @GET
    suspend fun fetchPluginPreviewInfo(@Url url: String): PreviewPluginInfo?
}