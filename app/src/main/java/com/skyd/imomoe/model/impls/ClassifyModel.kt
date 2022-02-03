package com.skyd.imomoe.model.impls

import android.app.Activity
import com.su.mediabox.plugin.interfaces.IClassifyModel
import com.su.mediabox.plugin.standard.been.AnimeCoverBean
import com.su.mediabox.plugin.standard.been.ClassifyBean
import com.su.mediabox.plugin.standard.been.PageNumberBean

class ClassifyModel : IClassifyModel {
    override suspend fun getClassifyData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }

    override fun clearActivity() {
    }

    override suspend fun getClassifyTabData(): ArrayList<ClassifyBean> {
        return ArrayList()
    }

    override fun setActivity(activity: Activity) {
    }
}
