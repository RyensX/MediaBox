package com.skyd.imomoe.util

import android.content.Context
import android.util.Log
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback

object PushHelper {
    fun init(context: Context) {
        //获取消息推送实例
        val pushAgent = PushAgent.getInstance(context)
        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(object : UPushRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                logI("PushHelper", "注册成功：deviceToken：--> $deviceToken")
            }

            override fun onFailure(errCode: String, errDesc: String) {
                logE("PushHelper", "注册失败：--> code:$errCode, desc:$errDesc")
            }
        })
    }
}