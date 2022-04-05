package com.su.mediabox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.bean.ResponseDataType
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.ClassifyBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IClassifyComponent
import com.su.mediabox.util.PluginIO
import com.su.mediabox.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Deprecated("V2后废弃")
class ClassifyViewModel : ViewModel() {
    private val classifyModel: IClassifyComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IClassifyComponent::class.java)
    }
    var isRequesting = false
    var classifyTabList: MutableList<ClassifyBean> = ArrayList()        //上方分类数据
    var mldClassifyTabList: MutableLiveData<Pair<List<ClassifyBean>, ResponseDataType>> =
        MutableLiveData()
    var classifyList: MutableList<AnimeCoverBean> = ArrayList()       //下方tv数据
    var mldClassifyList: MutableLiveData<Pair<ResponseDataType,List<AnimeCoverBean>>> =
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
        viewModelScope.launch(Dispatchers.PluginIO) {
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