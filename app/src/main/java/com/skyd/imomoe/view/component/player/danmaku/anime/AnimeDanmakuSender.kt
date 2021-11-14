package com.skyd.imomoe.view.component.player.danmaku.anime

import android.widget.Toast
import com.google.gson.Gson
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.danmaku.AnimeSendDanmakuBean
import com.skyd.imomoe.bean.danmaku.AnimeSendDanmakuResultBean
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.util.Text.shield
import com.skyd.imomoe.util.Util.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

object AnimeDanmakuSender {
    fun send(
        danmakuPlayer: DanmakuPlayer,
        ac: String,
        key: String,
        animeSendDanmakuBean: AnimeSendDanmakuBean,
    ) {
        if (animeSendDanmakuBean.text.shield()) {
            App.context.getString(R.string.danmaku_exist_shield_content).showToast(Toast.LENGTH_LONG)
            return
        }
        val time = danmakuPlayer.getCurrentTimeMs() + 500
        val danmaku = DanmakuItemData(
            Random.nextLong(),
            time,
            animeSendDanmakuBean.text,
            DanmakuItemData.DANMAKU_MODE_ROLLING,
            (0.28f * animeSendDanmakuBean.size.replace("px", "").toFloat()).toInt(),
            AnimeDanmakuParser.getColor(animeSendDanmakuBean.color),
            DanmakuItemData.DANMAKU_STYLE_ICON_UP
        )
        val item = danmakuPlayer.obtainItem(danmaku)
        val request = RetrofitManager.instance.create(DanmakuService::class.java) ?: return
        val json = Gson().toJson(animeSendDanmakuBean)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        request.sendDanmaku(ac, key, json).enqueue(object : Callback<AnimeSendDanmakuResultBean> {
            override fun onFailure(call: Call<AnimeSendDanmakuResultBean>, t: Throwable) {
                App.context.getString(R.string.send_danmaku_failed, t.message).showToast()
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<AnimeSendDanmakuResultBean>,
                response: Response<AnimeSendDanmakuResultBean>
            ) {
                response.body()?.message?.showToast()
                danmakuPlayer.send(item)
            }
        })
    }
}