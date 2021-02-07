package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.ParseHtmlUtil.parseTers
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class ClassifyViewModel : ViewModel() {
    private var requestTimes = 0
    var classifyTabList: MutableList<ClassifyBean> = ArrayList()        //上方分类数据
    var mldClassifyTabList: MutableLiveData<List<ClassifyBean>> = MutableLiveData()
    var classifyList: MutableList<AnimeCoverBean> = ArrayList()       //下方tv数据
    var mldClassifyList: MutableLiveData<Boolean> = MutableLiveData()

    fun getClassifyTabData() {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + "/a/").get()
                val areaElements: Elements = document.getElementsByClass("area")
                classifyTabList.clear()
                for (i in areaElements.indices) {
                    val areaChildren: Elements = areaElements[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "ters" -> {
                                classifyTabList.addAll(parseTers(areaChildren[j]))
                            }
                        }
                    }
                }
                mldClassifyTabList.postValue(classifyTabList)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    fun getClassifyData(partUrl: String) {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + partUrl).get()
                val areaElements: Elements = document.getElementsByClass("area")
                classifyList.clear()
                for (i in areaElements.indices) {
                    val areaChildren: Elements = areaElements[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "fire l" -> {
                                classifyList.addAll(parseLpic(areaChildren[j]))
                            }
                        }
                    }
                }
                mldClassifyList.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    companion object {
        const val TAG = "ClassifyViewModel"
    }
}