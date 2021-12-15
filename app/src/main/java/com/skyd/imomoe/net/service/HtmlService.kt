package com.skyd.imomoe.net.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface HtmlService {
    @GET
    suspend fun getHtml(@Url url: String, @Header("User-Agent") ua: String): ResponseBody

    @GET
    fun getHtmlSynchronously(@Url url: String, @Header("User-Agent") ua: String): Call<ResponseBody>
}