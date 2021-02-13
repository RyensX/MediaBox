package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class HomeViewModel : ViewModel() {
    var lTabList: MutableList<TabBean> = ArrayList()
    var mldGetLTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var rTabList: MutableList<TabBean> = ArrayList()
    var mldGetRTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var allTabList: MutableList<TabBean> = ArrayList()
    var mldGetAllTabList: MutableLiveData<List<TabBean>> = MutableLiveData()

    fun getAllTabData() {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL).get()
                val menu: Elements = document.getElementsByClass("menu")
                val dmx_l: Elements = menu.select("[class=dmx l]").select("li")
                allTabList.clear()
                for (i in dmx_l.indices) {
                    val url = dmx_l[i].select("a").attr("href")
                    allTabList.add(TabBean("", url, Api.MAIN_URL + url, dmx_l[i].text()))
                }
                val dme_r: Elements = menu.select("[class=dme r]").select("li")
                for (i in dme_r.indices) {
                    val url = dme_r[i].select("a").attr("href")
                    allTabList.add(TabBean("", url, Api.MAIN_URL + url, dme_r[i].text()))
                }
                mldGetAllTabList.postValue(allTabList)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                    Toast.LENGTH_LONG
                )
            }
        }.start()
    }

    fun getLTabData() {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL).get()
                val menu: Elements = document.getElementsByClass("menu")
                val dmx_l: Elements = menu.select("[class=dmx l]").select("li")
                lTabList.clear()
                for (i in dmx_l.indices) {
                    val url = dmx_l[i].select("a").attr("href")
                    lTabList.add(TabBean("", url, Api.MAIN_URL + url, dmx_l[i].text()))
                }
                mldGetLTabList.postValue(lTabList)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                    Toast.LENGTH_LONG
                )
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
                    rTabList.add(TabBean("", url, Api.MAIN_URL + url, dme_r[i].text()))
                }
                mldGetRTabList.postValue(rTabList)
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                    Toast.LENGTH_LONG
                )
            }
        }.start()
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}