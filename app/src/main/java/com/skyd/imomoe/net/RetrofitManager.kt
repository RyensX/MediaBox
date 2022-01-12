package com.skyd.imomoe.net

import com.skyd.imomoe.config.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager private constructor() {
    companion object {
        fun setInstanceNull() {
            instance = null
        }

        @Volatile
        private var instance: RetrofitManager? = null
            get() {
                if (field == null) {
                    synchronized(RetrofitManager::class) {
                        if (field == null) field = RetrofitManager()
                    }
                }
                return field
            }

        @Synchronized
        fun get(): RetrofitManager{
            return instance!!
        }
    }
//        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            RetrofitManager()
//        }

    private val builder = Retrofit.Builder()
        .baseUrl(Api.MAIN_URL)
        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器

    private var mRetrofit: Retrofit = builder.client(okhttpClient).build()

    fun client(client: OkHttpClient) {
        mRetrofit = builder.client(client).build()
    }

    fun <T> create(service: Class<T>): T {
        return mRetrofit.create(service)
    }
}