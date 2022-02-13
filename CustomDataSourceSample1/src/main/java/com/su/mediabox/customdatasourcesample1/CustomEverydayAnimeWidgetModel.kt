package com.su.mediabox.customdatasourcesample1

import com.su.mediabox.customdatasourcesample1.ParseHtmlUtil.parseTlist
import com.su.mediabox.plugin.interfaces.IEverydayAnimeWidgetModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import org.jsoup.select.Elements

class CustomEverydayAnimeWidgetModel : IEverydayAnimeWidgetModel {
    override fun getEverydayAnimeData(): ArrayList<List<AnimeCoverBean>> {
        val list: ArrayList<List<AnimeCoverBean>> = ArrayList()
        try {
            val document = JsoupUtil.getDocumentSynchronously(CustomConst.MAIN_URL())
            val areaChildren: Elements = document.select("[class=area]")[0].children()
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
                                            "tlist" -> {
                                                list.addAll(
                                                    parseTlist(bgChildren[k])
                                                )
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}