package com.skyd.imomoe.view.component.player

import android.graphics.Color
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.util.Text.shield
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class AnimeDanmakuParser : BaseDanmakuParser() {
    public override fun parse(): Danmakus {
        if (mDataSource != null && mDataSource is AnimeJSONSource) {
            val jsonSource = mDataSource as AnimeJSONSource
            return doParse(jsonSource.data())
        }
        return Danmakus()
    }

    /**
     * @param danmakuListData 弹幕数据
     * 传入的数组内包含普通弹幕，会员弹幕，锁定弹幕。
     * @return 转换后的Danmakus
     */
    private fun doParse(danmakuListData: JSONArray?): Danmakus {
        var danmakus = Danmakus()
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus
        }
        danmakus = _parse(danmakuListData, danmakus)
        return danmakus
    }

    private fun _parse(jsonArray: JSONArray?, danmakus: Danmakus): Danmakus {
        if (jsonArray == null || jsonArray.length() == 0) {
            return danmakus
        }
        for (i in 0 until jsonArray.length()) {
            try {
                val array = jsonArray.getJSONArray(i)
                val text = array.getString(4)
                // 如果此条弹幕应该屏蔽，则直接continue到下一条
                if (text.shield()) continue
                val type = getType(array.getString(1))          // 弹幕类型
                val time = (array.getDouble(0) * 1000).toLong() // 出现时间
                val color = getColor(array.getString(2))
                var textSize = 27.5f
                if (array.length() >= 8) {
                    textSize = array.getString(7).replace("px", "").toFloat()
                }
                val item = mContext.mDanmakuFactory.createDanmaku(type, mContext)
                if (item != null) {
                    item.time = time
                    item.textSize = 0.7f * textSize * (mDispDensity - 0.6f)
                    item.textColor = color
                    item.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
                    item.index = i
                    item.flags = mContext.mGlobalFlagValues
                    item.timer = mTimer
                    item.text = text
                    danmakus.addItem(item)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
        return danmakus
    }

    companion object {
        fun getColor(s: String): Int {
            val strColor = s.toLowerCase(Locale.ROOT)
            try {
                if (strColor.startsWith("#")) {
                    Color.parseColor(s)
                } else if (strColor.startsWith("rgb")) {
                    val rgbArray = strColor.replace("rgb(", "")
                        .replace(")", "").split(",")
                    if (rgbArray.size == 3) Color.rgb(
                        rgbArray[0].trim().toInt(),
                        rgbArray[1].trim().toInt(),
                        rgbArray[2].trim().toInt()
                    )
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            return Color.WHITE
        }
    }

    private fun getType(s: String): Int {
        return when (s) {
            "top" -> 4
            else -> 1
        }
    }
}