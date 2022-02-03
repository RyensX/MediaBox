package com.skyd.imomoe.model.impls

import android.app.Activity
import android.content.Context
import com.su.mediabox.plugin.interfaces.IRouteProcessor

class RouteProcessor : IRouteProcessor {
    override fun process(context: Context, actionUrl: String): Boolean {
        return false
    }

    override fun process(activity: Activity, actionUrl: String): Boolean {
        return false
    }
}
