package com.skyd.imomoe.view.widget.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.skyd.imomoe.R

class TypefaceTextView : AppCompatTextView {
    var isFocused: Boolean? = null

    fun setIsFocused(isFocused: Boolean?) {
        this.isFocused = isFocused
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TypefaceTextView, 0, 0)
            val typefaceType = typedArray.getInt(R.styleable.TypefaceTextView_typeface, 0)
            typeface = getTypeface(typefaceType)
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
            typeface =
                getTypeface(
                    typefaceType
                )
            typedArray.recycle()
        }
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