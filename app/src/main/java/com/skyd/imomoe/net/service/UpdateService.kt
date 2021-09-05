package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.UpdateBean
import com.skyd.imomoe.config.Api
import retrofit2.Call
import retrofit2.http.GET

interface UpdateService {
    @GET(Api.CHECK_UPDATE_URL)
    fun checkUpdate(): Call<UpdateBean>
}