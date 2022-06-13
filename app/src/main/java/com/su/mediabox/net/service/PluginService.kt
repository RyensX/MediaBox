package com.su.mediabox.net.service

import com.su.mediabox.model.PreviewPluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.util.Text.githubProxy
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PluginService {
    /**
     * 拉取插件预览信息，用于在线安装
     */
    @GET
    suspend fun fetchPluginPreviewInfo(@Url url: String): PreviewPluginInfo?

    @GET
    suspend fun fetchRepositoryPluginPreviewInfo(
        @Url url: String = Const.Plugin.GITHUB_OFFICIAL_REPOSITORY_PLUGIN_INFO_TEMPLATE.githubProxy
    ): List<PreviewPluginInfo>

    //分页获取插件仓库信息
    @GET
    suspend fun pageFetchRepositoryPluginPreviewInfo(
        @Path("page") page: Int,
        @Url url: String = Const.Plugin.GITHUB_OFFICIAL_REPOSITORY_PAGE_PLUGIN_INFO_TEMPLATE.githubProxy
    ): List<PreviewPluginInfo>
}