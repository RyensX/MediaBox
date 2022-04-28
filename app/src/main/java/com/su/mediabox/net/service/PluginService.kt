package com.su.mediabox.net.service

import com.su.mediabox.bean.PreviewPluginInfo
import com.su.mediabox.config.Const
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PluginService {
    /**
     * 拉取插件预览信息，用于在线安装
     */
    @GET
    suspend fun fetchPluginPreviewInfo(@Url url: String): PreviewPluginInfo?

    @GET(Const.Plugin.GITHUB_OFFICIAL_REPOSITORY_PLUGIN_INFO_TEMPLATE)
    suspend fun fetchRepositoryPluginPreviewInfo(@Path("page") page: Int): List<PreviewPluginInfo>
}