package com.skyd.imomoe.net

import com.skyd.imomoe.config.Api
import com.skyd.imomoe.net.DoH.doHInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor() {
    companion object {
        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

    private var mRetrofit: Retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor(doHInterceptor).build())
        .baseUrl(Api.MAIN_URL)
        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
        .build()

    fun <T> create(service: Class<T>): T {
        return mRetrofit.create(service)
    }
}