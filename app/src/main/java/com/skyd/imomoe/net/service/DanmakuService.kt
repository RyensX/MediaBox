package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.danmaku.AnimeSendDanmakuResultBean
import com.skyd.imomoe.config.Api
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface DanmakuService {
    @Headers(value = ["Content-Type: application/json", "Accept: application/json"])
    @POST(Api.DANMU_URL)
    fun sendDanmaku(
        @Query("ac") ac: String,
        @Query("key") key: String,
        @Body json: RequestBody
    ): Call<AnimeSendDanmakuResultBean>
}