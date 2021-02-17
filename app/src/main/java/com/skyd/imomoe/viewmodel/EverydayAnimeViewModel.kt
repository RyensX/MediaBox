package com.skyd.imomoe.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.ParseHtmlUtil.parseTlist
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class EverydayAnimeViewModel : ViewModel() {
    private var requestTimes = 0
    var header: AnimeShowBean = AnimeShowBean(
        "", "", "", "",
        "", "", "", null
    )
    var mldHeader: MutableLiveData<AnimeShowBean> = MutableLiveData()
    var tabList: MutableList<TabBean> = ArrayList()
    var mldTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var everydayAnimeList: MutableList<List<AnimeCoverBean>> = ArrayList()
    var mldEverydayAnimeList: MutableLiveData<List<List<AnimeCoverBean>>> = MutableLiveData()

    fun getEverydayAnimeData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(Api.MAIN_URL).get()
                val areaChildren: Elements = document.select("[class=area]")[0].children()
                tabList.clear()
                everydayAnimeList.clear()
                for (i in areaChildren.indices) {
                    when (areaChildren[i].className()) {
                        "side r" -> {
                            val sideRChildren = areaChildren[i].children()
                            out@ for (j in sideRChildren.indices) {
                                when (sideRChildren[j].className()) {
                                    "bg" -> {
                                        val bgChildren = sideRChildren[j].children()
                                        for (k in bgChildren.indices) {
                                            when (bgChildren[k].className()) {
                                                "dtit" -> {
                                                    header.title = parseDtit(bgChildren[k])
                                                    mldHeader.postValue(header)
                                                }
                                                "tag" -> {
                                                    val tagChildren = bgChildren[k].children()
                                                    for (l in tagChildren.indices) {
                                                        tabList.add(
                                                            TabBean(
                                                                "",
                                                                "",
                                                                "",
                                                                tagChildren[l].text()
                                                            )
                                                        )
                                                    }
                                                }
                                                "tlist" -> {
                                                    everydayAnimeList.addAll(parseTlist(bgChildren[k]))
                                                }
                                            }
                                        }
                                        break@out
                                    }
                                }
                            }
                        }
                    }
                }
                mldTabList.postValue(tabList)
                mldEverydayAnimeList.postValue(everydayAnimeList)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getEverydayAnimeData()
                    } else requestTimes = 0
                }
                Log.e(TAG, e.message ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToastOnThread(
                    Toast.LENGTH_LONG
                )
            }
        }
    }

    companion object {
        const val TAG = "EverydayAnimeViewModel"
    }
}