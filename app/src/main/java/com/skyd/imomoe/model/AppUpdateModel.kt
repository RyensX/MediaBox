package com.skyd.imomoe.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.skyd.imomoe.bean.UpdateBean
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.GithubService
import com.skyd.imomoe.util.Util.isNewVersion
import com.skyd.imomoe.util.update.AppUpdateStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AppUpdateModel {
    val status: MutableLiveData<AppUpdateStatus> = MutableLiveData()
    var updateBean: UpdateBean? = null
        private set

    init {
        status.value = AppUpdateStatus.UNCHECK
    }

    fun checkUpdate() {
        if (status.value == AppUpdateStatus.CHECKING) {
            return
        }
        status.value = AppUpdateStatus.CHECKING
        val request = RetrofitManager.instance.create(GithubService::class.java)
        request?.checkUpdate()?.enqueue(object : Callback<UpdateBean> {
            override fun onFailure(call: Call<UpdateBean>, t: Throwable) {
                Log.d("checkUpdate", t.message ?: "")
                status.postValue(AppUpdateStatus.ERROR)
            }

            override fun onResponse(call: Call<UpdateBean>, response: Response<UpdateBean>) {
                updateBean = response.body()
                updateBean?.let {
                    status.postValue(
                        if (isNewVersion(updateBean?.tagName ?: "0")) AppUpdateStatus.DATED
                        else AppUpdateStatus.VALID
                    )
                    return
                }
                status.postValue(AppUpdateStatus.ERROR)
            }
        })
    }
}