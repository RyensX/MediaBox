package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.util.showToast
import com.su.mediabox.plugin.interfaces.IClassifyModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.ClassifyBean
import com.su.mediabox.plugin.standard.been.PageNumberBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClassifyViewModel : ViewModel() {
    private val classifyModel: IClassifyModel by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IClassifyModel::class.java)
    }
    var isRequesting = false
    var classifyTabList: MutableList<ClassifyBean> = ArrayList()        //上方分类数据
    var mldClassifyTabList: MutableLiveData<Pair<MutableList<ClassifyBean>, ResponseDataType>> =
        MutableLiveData()
    var classifyList: MutableList<AnimeCoverBean> = ArrayList()       //下方tv数据
    var mldClassifyList: MutableLiveData<Pair<ResponseDataType, MutableList<AnimeCoverBean>>> =
        MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

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