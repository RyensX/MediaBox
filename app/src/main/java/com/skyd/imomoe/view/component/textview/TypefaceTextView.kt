package com.skyd.imomoe.view.component.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getBooleanOrThrow
import com.skyd.imomoe.R
import com.skyd.imomoe.util.logD

class TypefaceTextView : AppCompatTextView {
    var isFocused: Boolean? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TypefaceTextView, 0, 0)
            val typefaceType = typedArray.getInt(R.styleable.TypefaceTextView_typeface, 0)
            typeface = getTypeface(typefaceType)
            try {
                val focused = typedArray.getBooleanOrThrow(R.styleable.TypefaceTextView_focused)
                isFocused = focused
            } catch (e: IllegalArgumentException) {
                logD("TypefaceTextView", "has no focused attribute")
            }
            typedArray.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TypefaceTextView, 0, 0)
            val typefaceType = typedArray.getInt(R.styleable.TypefaceTextView_typeface, 0)
            typeface = getTypeface(typefaceType)
            try {
                val focused = typedArray.getBooleanOrThrow(R.styleable.TypefaceTextView_focused)
                isFocused = focused
            } catch (e: IllegalArgumentException) {
                logD("TypefaceTextView", "has no focused attribute")
            }
            typedArray.recycle()
        }
    }

    fun setTypeface(t: Int) {
        typeface = getTypeface(t)
    }

    override fun isFocused(): Boolean {
        isFocused?.let {
            return it
        }
        return super.isFocused()
    }

    companion object {
        fun getTypeface(typefaceType: Int?) = when (typefaceType) {
            TypefaceUtil.BPR_TYPEFACE -> TypefaceUtil.getBPRTypeface()
            else -> Typeface.DEFAULT
        }
    }
}