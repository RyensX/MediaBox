package com.su.mediabox.view.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.util.layoutInflater
import com.su.mediabox.util.setOnClickListener

class DanmakuColorSelector(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    companion object {
        private val defineColors = intArrayOf(
            Color.WHITE,
            0xFFE70012.toInt(),
            0xFFFEF102.toInt(),
            0xFF009843.toInt(),
            0xFF00A0EA.toInt(),
            0xFFE2027F.toInt(),
            0xFF002E72.toInt(),
            0xFFF0AB2A.toInt(),
            0xFF683A7B.toInt(),
            0xFF84C0CB.toInt(),
            0xFF927939.toInt()
        )
    }

    var danmakuColor: Int
        get() = Pref.danmakuSendColor.value
        private set(value) {
            Pref.danmakuSendColor.saveData(value)
        }

    init {
        orientation = HORIZONTAL

        val colors = RecyclerView(context).apply {
            adapter = ColorsAdapter()
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        addView(colors, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private inner class ColorsAdapter : RecyclerView.Adapter<ColorViewHolder>() {

        @SuppressLint("NotifyDataSetChanged")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder =
            ColorViewHolder(parent).apply {
                setOnClickListener(itemView) {
                    danmakuColor = defineColors[it]
                    notifyDataSetChanged()
                }
            }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.apply {
                val c = defineColors[position]
                color = c
                isSelectedColor = c == danmakuColor
            }
        }

        override fun getItemCount(): Int = defineColors.size

    }

    private class ColorViewHolder(colorBlock: View) :
        RecyclerView.ViewHolder(colorBlock) {
        constructor(parent: ViewGroup) : this(
            parent.context.layoutInflater.inflate(
                R.layout.item_danmaku_color,
                parent,
                false
            )
        )

        private val colorView = colorBlock.findViewById<TextView>(R.id.item_danmaku_color)
        private val colorSelectView =
            colorBlock.findViewById<ImageView>(R.id.item_danmaku_color_select)

        @ColorInt
        var color = Color.WHITE
            set(value) {
                colorView.setBackgroundColor(value)
                field = value
            }

        var isSelectedColor: Boolean
            get() = colorSelectView.isVisible
            set(value) {
                colorSelectView.isVisible = value
            }
    }
}