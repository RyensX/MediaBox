package com.skyd.imomoe.view.component

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.widget.TextView
import com.skyd.imomoe.R
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.sp
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.component.textview.TypefaceTextView


class AnimeToolbar : LinearLayout {
    var titleText: CharSequence? = null
        set(value) {
            titleTextView.text = value
            field = value
        }
    private var buttonDrawables: MutableList<Drawable?> = ArrayList()
    private var buttons: MutableList<ImageView> = ArrayList()
    private val buttonLayoutParams = LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private var backDrawableTitlePadding: Int = 10.dp

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AnimeToolbar, 0, 0)
        orientation = HORIZONTAL
        setToolbarBackground(a.getDrawable(R.styleable.AnimeToolbar_toolBarBackground))
        gravity = Gravity.CENTER_VERTICAL
        titleTextView = TypefaceTextView(context, attrs).apply {
            isFocused = true
            this.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
                marginStart = a.getDimensionPixelSize(
                    R.styleable.AnimeToolbar_backDrawableTitlePadding, 10.dp
                ).also { backDrawableTitlePadding = it }
                marginEnd = 12.dp
            }
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isFocusable = true
            marqueeRepeatLimit = -1
            isSingleLine = true
            a.getColorStateList(R.styleable.AnimeToolbar_titleColor)?.let { setTextColor(it) }
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                a.getDimensionPixelSize(R.styleable.AnimeToolbar_titleSize, 17.sp).toFloat()
            )
            setTypeface(a.getInt(R.styleable.AnimeToolbar_typeface, 0))
        }
        backButton = ImageView(context, attrs).apply {
            this.layoutParams = buttonLayoutParams
            adjustViewBounds = true
            context.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
                .also {
                    background = it.getDrawable(0)
                }.recycle()
            scaleType = ImageView.ScaleType.FIT_XY
            setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            imageTintList = a.getColorStateList(R.styleable.AnimeToolbar_backTint)
            setImageDrawable(
                a.getDrawable(R.styleable.AnimeToolbar_backDrawable)
                    ?: getResDrawable(R.drawable.ic_arrow_back_white_24)
            )
        }
        showBackButton(a.getBoolean(R.styleable.AnimeToolbar_showBackButton, true))
//        setContentInsetsRelative(0, 0)
        titleText = a.getString(R.styleable.AnimeToolbar_title)
        titleTextView.text = titleText
        addView(backButton)
        addView(titleTextView)
        a.getDrawable(R.styleable.AnimeToolbar_buttonDrawable1)?.let { addButton(it) }
        a.getDrawable(R.styleable.AnimeToolbar_buttonDrawable2)?.let { addButton(it) }
        a.getDrawable(R.styleable.AnimeToolbar_buttonDrawable3)?.let { addButton(it) }
        a.recycle()
    }

    fun setTitleColor(@ColorInt color: Int) {
        titleTextView.setTextColor(color)
    }

    fun setTitleColor(color: ColorStateList?) {
        titleTextView.setTextColor(color)
    }

    fun setToolbarBackgroundColor(color: Int) {
        setBackgroundColor(color)
    }

    fun setToolbarBackground(drawable: Drawable?) {
        background = drawable ?: context.resources.getColor(R.color.main_color_2_skin).toDrawable()
    }

    fun showBackButton(show: Boolean) {
        if (show) {
            titleTextView.layoutParams = (titleTextView.layoutParams as LayoutParams)
                .also { params -> params.marginStart = backDrawableTitlePadding }
            backButton.visible()
        } else {
            titleTextView.layoutParams = (titleTextView.layoutParams as LayoutParams)
                .also { params -> params.marginStart = 20.dp }
            backButton.gone()
        }
    }

    fun addButton(
        resource: Int,
        params: ViewGroup.LayoutParams = buttonLayoutParams
    ) {
        addButton(ResourcesCompat.getDrawable(context.resources, resource, null), params)
    }

    fun addButton(
        drawable: Drawable?,
        params: ViewGroup.LayoutParams = buttonLayoutParams
    ) {
        buttonDrawables.add(drawable)
        addView(ImageView(context).apply {
            layoutParams = params
            adjustViewBounds = true
            context.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
                .also { background = it.getDrawable(0) }.recycle()
            setPadding(12.dp, 12.dp, 12.dp, 12.dp)
            setImageDrawable(drawable)
            buttons.add(this)
        })
    }

    fun setButtonDrawable(index: Int, drawable: Drawable?) {
        if (index >= buttonDrawables.size) throw IndexOutOfBoundsException("index >= button count")
        else {
            buttonDrawables[index] = drawable
            buttons[index].setImageDrawable(drawable)
        }
    }

    fun setButtonDrawable(index: Int, resource: Int) {
        setButtonDrawable(index, ResourcesCompat.getDrawable(context.resources, resource, null))
    }

    fun setButtonEnable(index: Int, enable: Boolean) {
        if (index >= buttons.size) throw IndexOutOfBoundsException("index >= button count")
        else buttons[index].isEnabled = enable
    }

    fun setButtonClickListener(index: Int, listener: (() -> Unit)) {
        if (index >= buttons.size) throw IndexOutOfBoundsException("index >= button count")
        else buttons[index].setOnClickListener { listener.invoke() }
    }

    fun setBackButtonClickListener(listener: (() -> Unit)) {
        backButton.setOnClickListener { listener.invoke() }
    }
}