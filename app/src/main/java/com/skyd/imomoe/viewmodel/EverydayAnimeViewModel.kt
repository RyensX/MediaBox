package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeShowBean
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.html.JsoupUtil
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseDtit
import com.skyd.imomoe.util.html.ParseHtmlUtil.parseTlist
import com.skyd.imomoe.util.Util.getRealDayOfWeek
import com.skyd.imomoe.util.Util.showToastOnThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.select.Elements
import java.lang.Exception
import java.util.*


class EverydayAnimeViewModel : ViewModel() {
    var header: AnimeShowBean = AnimeShowBean(
        "", "", "", "",
        "", null, "", null
    )
    var selectedTabIndex = -1
    var mldHeader: MutableLiveData<AnimeShowBean> = MutableLiveData()
    var tabList: MutableList<TabBean> = ArrayList()
    var mldTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var everydayAnimeList: MutableList<List<AnimeCoverBean>> = ArrayList()
    var mldEverydayAnimeList: MutableLiveData<Boolean> = MutableLiveData()

    fun getEverydayAnimeData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = JsoupUtil.getDocument(Api.MAIN_URL)
                val areaChildren: Elements = document.select("[class=area]")[0].children()
                tabList.clear()
                val everydayAnimeList: MutableList<List<AnimeCoverBean>> = ArrayList()
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
                                                    selectedTabIndex = getRealDayOfWeek(
                                                        Calendar.getInstance(Locale.getDefault())
                                                            .get(Calendar.DAY_OF_WEEK)
                                                    ) - 1
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
                this@EverydayAnimeViewModel.everydayAnimeList.clear()
                this@EverydayAnimeViewModel.everydayAnimeList.addAll(everydayAnimeList)
                mldTabList.postValue(tabList)
                mldEverydayAnimeList.postValue(true)
            } catch (e: Exception) {
                selectedTabIndex = -1
                tabList.clear()
                everydayAnimeList.clear()
                mldEverydayAnimeList.postValue(false)
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