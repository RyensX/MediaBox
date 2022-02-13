package com.su.mediabox.customdatasourcesample1

import android.app.Activity
import com.su.mediabox.plugin.interfaces.IClassifyModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.ClassifyBean
import com.su.mediabox.plugin.standard.been.PageNumberBean
import org.jsoup.select.Elements

class CustomClassifyModel : IClassifyModel {
    override suspend fun getClassifyData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val classifyList: ArrayList<AnimeCoverBean> = ArrayList()
        var pageNumberBean: PageNumberBean? = null
        val url = CustomConst.MAIN_URL() + partUrl
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

    override suspend fun getClassifyTabData(): ArrayList<ClassifyBean> {
        val classifyTabList: ArrayList<ClassifyBean> = ArrayList()
        val document = JsoupUtil.getDocument(CustomConst.MAIN_URL() + "/a/")
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
        return classifyTabList
    }
}
