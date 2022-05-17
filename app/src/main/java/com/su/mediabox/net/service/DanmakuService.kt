package com.su.mediabox.net.service

import com.su.mediabox.bean.danmaku.AnimeSendDanmakuResultBean
import com.su.mediabox.config.Api
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DanmakuService {
    @Headers(value = ["Content-Type: application/json", "Accept: application/json"])
    @POST(Api.DANMU_URL)
    fun sendDanmaku(
        @Query("ac") ac: String,
        @Query("key") key: String,
        @Body json: RequestBody
    ): Call<AnimeSendDanmakuResultBean>

    @GET
    fun getDanmaku(@Url url: String): Call<ResponseBody>

}