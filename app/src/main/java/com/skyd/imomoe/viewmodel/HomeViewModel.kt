package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.HomeModel
import com.skyd.imomoe.model.interfaces.IHomeModel
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class HomeViewModel : ViewModel() {
    private val homeModel: IHomeModel by lazy {
        DataSourceManager.create(IHomeModel::class.java) ?: HomeModel()
    }
    val childViewPool = SerializableRecycledViewPool()
    val viewPool = SerializableRecycledViewPool()
    var allTabList: MutableList<TabBean> = ArrayList()
    var mldGetAllTabList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllTabData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                homeModel.getAllTabData(object : IHomeModel.AllTabDataCallBack {
                    override fun onSuccess(list: ArrayList<TabBean>) {
                        allTabList.clear()
                        allTabList.addAll(list)
                        mldGetAllTabList.postValue(true)
                    }

                    override fun onError(e: Exception) {
                        allTabList.clear()
                        mldGetAllTabList.postValue(false)
                        e.printStackTrace()
                        (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                            Toast.LENGTH_LONG
                        )
                    }

                }).apply {
                    this ?: return@apply
                    allTabList.clear()
                    allTabList.addAll(this)
                    mldGetAllTabList.postValue(true)
                }

            } catch (e: Exception) {
                allTabList.clear()
                mldGetAllTabList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                    Toast.LENGTH_LONG
                )
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}