package com.su.mediabox.net.service

import com.su.mediabox.bean.UpdateBean
import com.su.mediabox.config.Api
import retrofit2.Call
import retrofit2.http.GET

interface UpdateService {
    @GET(Api.CHECK_UPDATE_URL)
    fun checkUpdate(): Call<UpdateBean>

    @GET("https://api.github.com/repos/RyensX/MediaBox/releases")
    suspend fun getReleases(): List<UpdateBean>
}