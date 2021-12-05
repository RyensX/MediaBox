package com.skyd.imomoe.model.impls.custom

import android.app.Activity
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.UnknownActionUrl
import com.skyd.imomoe.model.util.JsoupUtil
import com.skyd.imomoe.model.interfaces.IClassifyModel
import com.skyd.imomoe.view.activity.ClassifyActivity
import org.jsoup.select.Elements

class CustomClassifyModel : IClassifyModel {
    override suspend fun getClassifyData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val classifyList: ArrayList<AnimeCoverBean> = ArrayList()
        var pageNumberBean: PageNumberBean? = null
        val url = Api.MAIN_URL + partUrl
        val document = JsoupUtil.getDocument(url)
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "fire l" -> {
                        val fireLChildren: Elements = areaChildren[j].children()
                        for (k in fireLChildren.indices) {
                            when (fireLChildren[k].className()) {
                                "lpic" -> {
                                    classifyList.addAll(
                                        ParseHtmlUtil.parseLpic(
                                            fireLChildren[k],
                                            url
                                        )
                                    )
                                }
                                "pages" -> {
                                    pageNumberBean = ParseHtmlUtil.parseNextPages(fireLChildren[k])
                                }
                            }
                        }
                    }
                }
            }
        }
        return Pair(classifyList, pageNumberBean)
    }

    override fun clearActivity() {
    }

    override suspend fun getClassifyTabData(): ArrayList<ClassifyBean> {
        val classifyTabList: ArrayList<ClassifyBean> = ArrayList()
        val document = JsoupUtil.getDocument(Api.MAIN_URL + "/a/")
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "ters" -> {
                        classifyTabList.addAll(ParseHtmlUtil.parseTers(areaChildren[j]))
                    }
                }
            }
        }
        classifyTabList.forEach {
            it.classifyDataList.forEach { item ->
                UnknownActionUrl.actionMap[item.actionUrl] =
                    object : UnknownActionUrl.Action {
                        override fun action() {
                            App.context.startActivity(
                                Intent(App.context, ClassifyActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra("partUrl", item.actionUrl)
                            )
                        }
                    }
            }
        }
        return classifyTabList
    }

    override fun setActivity(activity: Activity) {
    }
}
