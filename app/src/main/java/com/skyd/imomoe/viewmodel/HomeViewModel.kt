package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.HomeTabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class HomeViewModel : ViewModel() {
    private var requestTimes = 0
    var lTabList: MutableList<HomeTabBean> = ArrayList()
    var mldGetLTabList: MutableLiveData<List<HomeTabBean>> = MutableLiveData()
    var rTabList: MutableList<HomeTabBean> = ArrayList()
    var mldGetRTabList: MutableLiveData<List<HomeTabBean>> = MutableLiveData()

    fun getLTabData() {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL).get()
                val menu: Elements = document.getElementsByClass("menu")
                val dmx_l: Elements = menu.select("[class=dmx l]").select("li")
                lTabList.clear()
                for (i in dmx_l.indices) {
                    val url = dmx_l[i].select("a").attr("href")
                    lTabList.add(HomeTabBean("", url, Api.MAIN_URL + url, dmx_l[i].text()))
                }
                mldGetLTabList.postValue(lTabList)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getLTabData()
                    } else requestTimes = 0
                }
                Log.e(TAG, e.message ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    fun getRTabData() {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL).get()
                val menu: Elements = document.getElementsByClass("menu")
                val dme_r: Elements = menu.select("[class=dme r]").select("li")
                rTabList.clear()
                for (i in dme_r.indices) {
                    val url = dme_r[i].select("a").attr("href")
                    rTabList.add(HomeTabBean("", url, Api.MAIN_URL + url, dme_r[i].text()))
                }
                mldGetRTabList.postValue(rTabList)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getRTabData()
                    } else requestTimes = 0
                }
                Log.e(TAG, e.message ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread()
            }
        }.start()
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}