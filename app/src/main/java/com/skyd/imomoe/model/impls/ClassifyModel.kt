package com.skyd.imomoe.model.impls

import android.app.Activity
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.interfaces.IClassifyModel

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
