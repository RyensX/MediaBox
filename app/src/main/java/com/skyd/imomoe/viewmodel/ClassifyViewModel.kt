package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.ClassifyModel
import com.skyd.imomoe.model.interfaces.IClassifyModel
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class ClassifyViewModel : ViewModel() {
    private val classifyModel: IClassifyModel by lazy {
        DataSourceManager.create(IClassifyModel::class.java) ?: ClassifyModel()
    }
    var isRequesting = false
    var classifyTabList: MutableList<ClassifyBean> = ArrayList()        //上方分类数据
    var mldClassifyTabList: MutableLiveData<Boolean> = MutableLiveData()
    var classifyList: MutableList<AnimeCoverBean> = ArrayList()       //下方tv数据
    var mldClassifyList: MutableLiveData<Int> = MutableLiveData()       // value：-1错误；0重新获取；1刷新
    var pageNumberBean: PageNumberBean? = null
    var newPageIndex: Pair<Int, Int>? = null

    fun setActivity(activity: Activity) {
        classifyModel.setActivity(activity)
    }

    fun clearActivity() {
        classifyModel.clearActivity()
    }

    fun getClassifyTabData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                classifyModel.getClassifyTabData(object :
                    IClassifyModel.ClassifyTabDataCallBack {
                    override fun onSuccess(list: ArrayList<ClassifyBean>) {
                        classifyTabList.clear()
                        classifyTabList.addAll(list)
                        mldClassifyTabList.postValue(true)
                    }

                    override fun onError(e: Exception) {
                        throw e
                    }

                }).apply {
                    if (this != null) {
                        classifyTabList.clear()
                        classifyTabList.addAll(this)
                        mldClassifyTabList.postValue(true)
                    }
                }
            } catch (e: Exception) {
                classifyTabList.clear()
                mldClassifyTabList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    fun getClassifyData(partUrl: String, isRefresh: Boolean = true) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (isRequesting) return@launch
                isRequesting = true
                classifyModel.getClassifyData(partUrl, object :
                    IClassifyModel.ClassifyDataCallBack {
                    override fun onSuccess(p: com.skyd.imomoe.model.util.Pair<ArrayList<AnimeCoverBean>, PageNumberBean>) {
                        if (isRefresh) classifyList.clear()
                        val positionStart = classifyList.size
                        classifyList.addAll(p.first)
                        pageNumberBean = p.second
                        newPageIndex = Pair(positionStart, classifyList.size - positionStart)
                        mldClassifyList.postValue(if (isRefresh) 0 else 1)
                    }

                    override fun onError(e: java.lang.Exception) {
                        pageNumberBean = null
                        classifyList.clear()
                        mldClassifyList.postValue(-1)
                        e.printStackTrace()
                        (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
                    }

                }).apply {
                    this ?: return@apply
                    if (isRefresh) classifyList.clear()
                    val positionStart = classifyList.size
                    classifyList.addAll(first)
                    pageNumberBean = second
                    newPageIndex = Pair(positionStart, classifyList.size - positionStart)
                    mldClassifyList.postValue(if (isRefresh) 0 else 1)
                }
            } catch (e: Exception) {
                pageNumberBean = null
                classifyList.clear()
                mldClassifyList.postValue(-1)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }
    }

    companion object {
        const val TAG = "ClassifyViewModel"
    }
}