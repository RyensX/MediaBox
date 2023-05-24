package com.su.mediabox.net.service

import com.su.mediabox.bean.UpdateBean
import com.su.mediabox.config.Api
import com.su.mediabox.config.Const
import com.su.mediabox.util.Announcement
import com.su.mediabox.util.Text.githubProxy
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface AppService {
    @GET(Api.CHECK_UPDATE_URL)
    fun checkUpdate(): Call<UpdateBean>

    @GET("https://api.github.com/repos/RyensX/MediaBox/releases")
    suspend fun getReleases(): List<UpdateBean>

    @GET
    suspend fun getAnnouncement(@Url url: String = Const.Common.ANNOUNCEMENT.githubProxy): Announcement
}