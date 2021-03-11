package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.JsoupUtil
import com.skyd.imomoe.util.Util.showToastOnThread
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class HomeViewModel : ViewModel() {
    val childViewPool = SerializableRecycledViewPool()
    val viewPool = SerializableRecycledViewPool()
    var allTabList: MutableList<TabBean> = ArrayList()
    var mldGetAllTabList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllTabData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = JsoupUtil.getDocument(Api.MAIN_URL)
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
                mldGetAllTabList.postValue(true)
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