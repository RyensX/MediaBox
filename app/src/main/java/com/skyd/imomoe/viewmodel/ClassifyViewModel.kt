package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.ClassifyModel
import com.skyd.imomoe.model.interfaces.IClassifyModel
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClassifyViewModel : ViewModel() {
    private val classifyModel: IClassifyModel by lazy {
        DataSourceManager.create(IClassifyModel::class.java) ?: ClassifyModel()
    }
    var isRequesting = false
    var classifyTabList: MutableList<ClassifyBean> = ArrayList()        //上方分类数据
    var mldClassifyTabList: MutableLiveData<Pair<MutableList<ClassifyBean>, ResponseDataType>> =
        MutableLiveData()
    var classifyList: MutableList<AnimeCoverBean> = ArrayList()       //下方tv数据
    var mldClassifyList: MutableLiveData<Pair<ResponseDataType, MutableList<AnimeCoverBean>>> =
        MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

    fun setActivity(activity: Activity) {
        classifyModel.setActivity(activity)
    }

    fun clearActivity() {
        classifyModel.clearActivity()
    }

    fun getClassifyTabData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mldClassifyTabList.postValue(
                    Pair(classifyModel.getClassifyTabData(), ResponseDataType.REFRESH)
                )
            } catch (e: Exception) {
                classifyTabList.clear()
                mldClassifyTabList.postValue(Pair(ArrayList(), ResponseDataType.FAILED))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    fun getClassifyData(partUrl: String, isRefresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                classifyModel.getClassifyData(partUrl).apply {
                    pageNumberBean = second
                    mldClassifyList.postValue(
                        Pair(if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE, first)
                    )
                }
            } catch (e: Exception) {
                pageNumberBean = null
                mldClassifyList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    companion object {
        const val TAG = "ClassifyViewModel"
    }
}