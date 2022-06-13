package com.su.mediabox.view.viewcomponents

import android.view.View
import android.widget.TextView
import com.su.mediabox.R
import com.su.mediabox.pluginapi.data.TextData
import com.su.mediabox.util.Util
import com.su.mediabox.view.adapter.type.TypeViewHolder

abstract class TextViewHolder<T : TextData>(
    protected val textView: TextView,
    itemView: View = textView
) :
    TypeViewHolder<T>(itemView) {

    protected var textData: T? = null
    protected val styleColor = Util.getResColor(R.color.main_color_2_skin)

    override fun onBind(data: T) {
        super.onBind(data)
        textData = data
        textView.apply {
            //字体模式
            setTypeface(typeface, data.fontStyle)
            //字体颜色
            setTextColor(data.fontColor ?: styleColor)
            //字体大小
            textSize = data.fontSize
            //对齐方式
            gravity = data.gravity
        }
    }
}