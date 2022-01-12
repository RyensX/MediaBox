package com.skyd.imomoe.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.skyd.imomoe.App
import com.skyd.imomoe.bean.UpdateBean
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.UpdateService
import com.skyd.imomoe.util.Util.isNewVersion
import com.skyd.imomoe.util.editor
import com.skyd.imomoe.util.sharedPreferences
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AppUpdateModel {
    val status: MutableLiveData<AppUpdateStatus> = MutableLiveData()
    var updateBean: UpdateBean? = null
        private set

    var updateServer: Int = AppUpdateHelper.GITHUB
        set(value) {
            if (value == field) return
            field = if (value in AppUpdateHelper.serverName.indices) {
                App.context.sharedPreferences("update").editor {
                    putInt(AppUpdateHelper.UPDATE_SERVER_SP_KEY, value)
                }
                value
            } else {
                0
            }
            mldUpdateServer.postValue(field)
        }

    var mldUpdateServer: MutableLiveData<Int> = MutableLiveData()

    init {
        status.value = AppUpdateStatus.UNCHECK
        updateServer =
            App.context.sharedPreferences("update").getInt(AppUpdateHelper.UPDATE_SERVER_SP_KEY, 0)
    }

    fun checkUpdate() {
        if (status.value == AppUpdateStatus.CHECKING) {
            return
        }
        status.value = AppUpdateStatus.CHECKING
        val request = RetrofitManager.get().create(UpdateService::class.java)
        val check = request?.checkUpdate()
        check?.enqueue(object : Callback<UpdateBean> {
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