package com.skyd.imomoe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeTypeBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_SEARCH
import com.skyd.imomoe.util.ParseHtmlUtil.parseLpic
import com.skyd.imomoe.util.Util.showToastOnThread
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import kotlin.collections.ArrayList


class SearchViewModel : ViewModel() {
    private var requestTimes = 0
    var searchResultList: MutableList<AnimeCoverBean> = ArrayList()
    var mldSearchResultList: MutableLiveData<String> = MutableLiveData()

    fun getSearchData(keyWord: String) {
        Thread {
            try {
                val document = Jsoup.connect(Api.MAIN_URL + ANIME_SEARCH + keyWord).get()
                val lpic: Elements = document.getElementsByClass("area")
                    .select("[class=fire l]").select("[class=lpic]")
                searchResultList.clear()
                searchResultList.addAll(parseLpic(lpic[0]))
                mldSearchResultList.postValue(keyWord)
            } catch (e: HttpStatusException) {
                e.printStackTrace()
                if (e.statusCode == 502) {
                    if (requestTimes <= 2) {
                        requestTimes++
                        getSearchData(keyWord)
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
        const val TAG = "SearchViewModel"
    }
}