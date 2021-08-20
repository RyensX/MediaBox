package com.skyd.skin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.skin.core.SkinAttrsSet
import com.skyd.skin.core.SkinPreferenceUtil
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.*
import com.skyd.skin.core.listeners.ChangeCustomSkinListener
import com.skyd.skin.core.listeners.ChangeSkinListener
import java.lang.reflect.Constructor
import java.util.*

object SkinManager {
    var KEY_SKIN_PATH = "skin_path"
    var KEY_SKIN_SUFFIX = "skin_suffix"
    var KEY_SKIN_DARK = "skin_dark"
    var KEY_SKIN_DARK_SUFFIX = "_dark"
    const val DARK_FOLLOW_SYSTEM = 0
    const val DARK_MODE_YES = 1
    const val DARK_MODE_NO = 2
    private val changeSkinListeners: MutableList<ChangeSkinListener> = ArrayList()

    private val attrIds = intArrayOf(
        R.attr.background,
        android.R.attr.background,
        android.R.attr.src,
        android.R.attr.textColor,
        android.R.attr.windowBackground,
        android.R.attr.drawableStart,
        android.R.attr.drawableEnd,
        R.attr.tabIndicatorColor,
        R.attr.tabTextColor,
        android.R.attr.scrollbarThumbVertical,
        R.attr.tint,
        android.R.attr.backgroundTint,
        android.R.attr.textColorHint,
        R.attr.drawableTopCompat,
        R.attr.colorPrimary,
        R.attr.srlPrimaryColor,
        R.attr.thumbTint,
        R.attr.trackTint,
        android.R.attr.buttonTint,
        R.attr.cardBackgroundColor,
        android.R.attr.indeterminateTint,
        android.R.attr.thumb,
        android.R.attr.progressDrawable
    ).apply { sort() }      // 需要升序排序，所以要调用sort()

    //todo 反射：这里使用反射创建View
    private val sConstructorMap: MutableMap<String, Constructor<out View>> = HashMap()
    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)

    fun init(application: Application) {
        SkinResourceProcessor.init(application)
    }

    fun addListener(listener: ChangeSkinListener) {
        changeSkinListeners.add(listener)
    }

    // 设置深色模式请不要直接调用此方法，请使用setDarkMode方法
    fun notifyListener(skinPath: String, skinSuffix: String = "") {
        val startTime = System.currentTimeMillis()
        loadSkinResources(skinPath, skinSuffix)
        for (listener in changeSkinListeners) {
            listener.onChangeSkin()
        }
        Log.e("ChangeSkin", "spend time: ${System.currentTimeMillis() - startTime}")
    }

    fun removeListener(listener: ChangeSkinListener) {
        changeSkinListeners.remove(listener)
    }

    fun onCreateView(
        activity: AppCompatActivity,
        inflater: LayoutInflater?,
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        var view = activity.delegate.createView(parent, name, context, attrs)
        if (view == null) view = createViewFromTag(context, name, attrs)
        if (view != null) {
            setSkinTag(context, attrs, view)
            setSkin(view)
        }
        return view
    }

    //不能使用反射调用inflater的onCreateView方法，有的Layout会创建失败
    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == name.indexOf('.')) {
                if ("View" == name) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs)
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs)
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs)
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs)
            }
        } catch (e: Exception) {
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
            view = null
        }
        return view
    }

    private fun createView(context: Context, name: String, prefix: String?): View? {
        val constructor = sConstructorMap[name].let {
            if (it == null) {
                val clazz = context.classLoader
                    .loadClass(if (prefix != null) prefix + name else name)
                    .asSubclass(View::class.java)
                val constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor
                constructor
            } else it
        }
        constructor.isAccessible = true
        return constructor.newInstance(*mConstructorArgs)
    }

    @SuppressLint("ResourceType", "PrivateResource")
    private fun setSkinTag(context: Context, attrs: AttributeSet, view: View) {
        val typedArray = context.obtainStyledAttributes(attrs, attrIds)
        val skinAttrsSet = SkinAttrsSet()
        val defValue = -1
        attrIds.forEachIndexed { index, i ->
            var resId = typedArray.getResourceId(index, defValue)
            when (i) {
                R.attr.background, android.R.attr.background -> {
                    if (resId != -1) skinAttrsSet.attrsMap[BackgroundAttr.TAG] =
                        BackgroundAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.src -> {
                    if (resId != -1) skinAttrsSet.attrsMap[SrcAttr.TAG] =
                        SrcAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.textColor -> {
                    if (resId == -1 && view is TextView) {
                        resId = getTextColorResId(attrs)
                    }
                    if (resId != -1) skinAttrsSet.attrsMap[TextColorAttr.TAG] =
                        TextColorAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.drawableStart -> {
                    if (resId != -1) skinAttrsSet.attrsMap[DrawableStartAttr.TAG] =
                        DrawableStartAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.drawableEnd -> {
                    if (resId != -1) skinAttrsSet.attrsMap[DrawableEndAttr.TAG] =
                        DrawableEndAttr().apply { attrResourceRefId = resId }
                }
                R.attr.tabIndicatorColor -> {
                    if (resId != -1) skinAttrsSet.attrsMap[TabIndicatorColorAttr.TAG] =
                        TabIndicatorColorAttr().apply { attrResourceRefId = resId }
                }
                R.attr.tabTextColor -> {
                    if (resId == -1 && view is TabLayout) {
                        resId = getTextColorResId(attrs)
                    }
                    if (resId != -1) skinAttrsSet.attrsMap[TabTextColorAttr.TAG] =
                        TabTextColorAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.scrollbarThumbVertical -> {
                    if (resId != -1) skinAttrsSet.attrsMap[ScrollbarThumbVerticalAttr.TAG] =
                        ScrollbarThumbVerticalAttr().apply { attrResourceRefId = resId }
                }
                R.attr.tint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[ImageViewTintAttr.TAG] =
                        ImageViewTintAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.backgroundTint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[BackgroundTintAttr.TAG] =
                        BackgroundTintAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.textColorHint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[TextColorHintAttr.TAG] =
                        TextColorHintAttr().apply { attrResourceRefId = resId }
                }
                R.attr.drawableTopCompat -> {
                    if (resId != -1) skinAttrsSet.attrsMap[DrawableTopCompatAttr.TAG] =
                        DrawableTopCompatAttr().apply { attrResourceRefId = resId }
                }
                R.attr.colorPrimary -> {
                    if (resId != -1) skinAttrsSet.attrsMap[ColorPrimaryAttr.TAG] =
                        ColorPrimaryAttr().apply { attrResourceRefId = resId }
                }
                R.attr.srlPrimaryColor -> {
                    if (resId != -1) skinAttrsSet.attrsMap[SrlPrimaryColorAttr.TAG] =
                        SrlPrimaryColorAttr().apply { attrResourceRefId = resId }
                }
                R.attr.thumbTint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[ThumbTintAttr.TAG] =
                        ThumbTintAttr().apply { attrResourceRefId = resId }
                }
                R.attr.trackTint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[TrackTintAttr.TAG] =
                        TrackTintAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.buttonTint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[ButtonTintAttr.TAG] =
                        ButtonTintAttr().apply { attrResourceRefId = resId }
                }
                R.attr.cardBackgroundColor -> {
                    if (resId != -1) skinAttrsSet.attrsMap[CardBackgroundColorAttr.TAG] =
                        CardBackgroundColorAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.indeterminateTint -> {
                    if (resId != -1) skinAttrsSet.attrsMap[IndeterminateTintAttr.TAG] =
                        IndeterminateTintAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.thumb -> {
                    if (resId != -1)
                        skinAttrsSet.attrsMap[ThumbAttr.TAG] =
                            ThumbAttr().apply { attrResourceRefId = resId }
                }
                android.R.attr.progressDrawable -> {
                    if (resId != -1)
                        skinAttrsSet.attrsMap[ProgressDrawableAttr.TAG] =
                            ProgressDrawableAttr().apply { attrResourceRefId = resId }
                }
            }
        }
        typedArray.recycle()
        view.setTag(R.id.change_skin_tag, skinAttrsSet)
        if (view is ChangeCustomSkinListener) {
            view.setCustomTag()
        }
    }

    // 设置深色模式请不要直接调用此方法，请使用setDarkMode方法
    fun loadSkinResources(skinPath: String, skinSuffix: String = "") {
        SkinResourceProcessor.instance.application.apply {
            if (skinPath != "" || skinSuffix != KEY_SKIN_DARK_SUFFIX) {
                SkinPreferenceUtil.putString(this, KEY_SKIN_PATH, skinPath)
                SkinPreferenceUtil.putString(this, KEY_SKIN_SUFFIX, skinSuffix)
                if (SkinPreferenceUtil.getInt(this, KEY_SKIN_DARK, DARK_MODE_NO) !=
                    DARK_FOLLOW_SYSTEM
                ) {
                    SkinPreferenceUtil.putInt(this, KEY_SKIN_DARK, DARK_MODE_NO)
                } else if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES
                ) {
                    SkinPreferenceUtil.putInt(this, KEY_SKIN_DARK, DARK_MODE_NO)
                }
                SkinResourceProcessor.instance.loadSkinResources(skinPath, skinSuffix)
            } else {
                SkinResourceProcessor.instance.loadSkinResources(skinPath, skinSuffix)
            }
        }
    }

    fun applyViews(view: View?) {
        setSkin(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                applyViews(view.getChildAt(i))
            }
        }
    }

    fun setSkin(view: View?) {
        view ?: return
        val tag = view.getTag(R.id.change_skin_tag)
        if (tag != null) {
            val skin = tag as SkinAttrsSet
            for ((_, v) in skin.attrsMap) {
                v.applySkin(view)
            }
            if (view is ChangeCustomSkinListener) {
                view.changeCustomSkin()
            }
        }
    }

    fun setDarkMode(b: Int) {
        SkinResourceProcessor.instance.setDarkMode(b)
    }

    fun getDarkMode(): Int {
        return SkinResourceProcessor.instance.getDarkMode()
    }

    fun systemDarkModeChanged(newConfig: Configuration) {
        SkinResourceProcessor.instance.systemDarkModeChanged(newConfig)
    }

    fun autoLoadSkinResources() {
        SkinResourceProcessor.instance.autoLoadSkinResources()
    }

    fun setViewBackground(view: View?, background: Int) {
        view ?: return
        val tag = view.getTag(R.id.change_skin_tag)
        if (tag is SkinAttrsSet) {
            tag.attrsMap[BackgroundAttr.TAG].let {
                if (it == null) tag.attrsMap[BackgroundAttr.TAG] = BackgroundAttr().apply {
                    attrResourceRefId = background
                } else it.attrResourceRefId = background
            }
            view.tag = tag
        }
    }

    fun setViewSrc(imageView: ImageView?, src: Int) {
        imageView ?: return
        val tag = imageView.getTag(R.id.change_skin_tag)
        if (tag is SkinAttrsSet) {
            tag.attrsMap[SrcAttr.TAG].let {
                if (it == null) tag.attrsMap[SrcAttr.TAG] = SrcAttr().apply {
                    attrResourceRefId = src
                } else it.attrResourceRefId = src
            }
            imageView.tag = tag
        }
    }

    fun setViewTextColor(textView: TextView?, textColor: Int) {
        textView ?: return
        val tag = textView.getTag(R.id.change_skin_tag)
        if (tag is SkinAttrsSet) {
            tag.attrsMap[TextColorAttr.TAG].let {
                if (it == null) tag.attrsMap[TextColorAttr.TAG] = TextColorAttr().apply {
                    attrResourceRefId = textColor
                } else it.attrResourceRefId = textColor
            }
            textView.tag = tag
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setScrollbarThumbVertical(view: View, resourceId: Int) {
        if (resourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val drawable = ContextCompat.getDrawable(view.context, resourceId)
                view.verticalScrollbarThumbDrawable = drawable
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(resourceId)
                if (skinResourceId is Int) {
                    view.verticalScrollbarThumbDrawable = ColorDrawable(skinResourceId)
                } else {
                    val drawable = skinResourceId as Drawable
                    view.verticalScrollbarThumbDrawable = drawable
                }
            }
        }
    }

    fun setBackground(view: View, backgroundResourceId: Int) {
        if (backgroundResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val drawable = ContextCompat.getDrawable(view.context, backgroundResourceId)
                view.background = drawable
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(backgroundResourceId)
                if (skinResourceId is Int) {
                    view.setBackgroundColor(skinResourceId)
                } else {
                    val drawable = skinResourceId as Drawable
                    view.background = drawable
                }
            }
        }
    }

    fun setBackgroundTint(view: View, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.backgroundTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.backgroundTintList = color
            }
        }
    }

    fun setThumb(view: SeekBar, backgroundResourceId: Int) {
        if (backgroundResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val drawable = ContextCompat.getDrawable(view.context, backgroundResourceId)
                view.thumb = drawable
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(backgroundResourceId)
                if (skinResourceId is Int) {
                    view.setBackgroundColor(skinResourceId)
                } else {
                    val drawable = skinResourceId as Drawable
                    view.thumb = drawable
                }
            }
        }
    }

    fun setProgressDrawable(view: ProgressBar, backgroundResourceId: Int) {
        if (backgroundResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val drawable = ContextCompat.getDrawable(view.context, backgroundResourceId)
                view.progressDrawable = drawable
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(backgroundResourceId)
                if (skinResourceId is Int) {
                    view.progressDrawable = ColorDrawable(skinResourceId)
                } else {
                    val drawable = skinResourceId as Drawable
                    view.progressDrawable = drawable
                }
            }
        }
    }

    fun setSrc(view: ImageView, srcResourceId: Int) {
        if (srcResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                // 兼容包转换
                view.setImageResource(srcResourceId)
                val drawable = ContextCompat.getDrawable(view.context, srcResourceId)
                view.setImageDrawable(drawable)
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(srcResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setImageResource(skinResourceId)
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    val drawable = skinResourceId as Drawable
                    view.setImageDrawable(drawable)
                }
            }
        }
    }

    fun setDrawableTopCompat(view: TextView, srcResourceId: Int) {
        if (srcResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    ContextCompat.getDrawable(view.context, srcResourceId),
                    null,
                    null
                )
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(srcResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        ContextCompat.getDrawable(view.context, srcResourceId),
                        null,
                        null
                    )
                } else {
                    val drawable = skinResourceId as Drawable
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                }
            }
        }
    }

    fun setDrawableStart(view: TextView, srcResourceId: Int) {
        if (srcResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.getDrawable(view.context, srcResourceId),
                    null, null, null
                )
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(srcResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(view.context, srcResourceId),
                        null, null, null
                    )
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    val drawable = skinResourceId as Drawable
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        }
    }

    fun setDrawableEnd(view: TextView, srcResourceId: Int) {
        if (srcResourceId > 0) {
            // 是否默认皮肤
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null, ContextCompat.getDrawable(view.context, srcResourceId), null
                )
            } else {
                // 获取皮肤包资源
                val skinResourceId = skinResProcessor.getBackgroundOrSrc(srcResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null, ContextCompat.getDrawable(view.context, srcResourceId), null
                    )
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    val drawable = skinResourceId as Drawable
                    view.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
                }
            }
        }
    }

    fun setTabTextColor(view: TabLayout, textColorResourceId: Int) {
        if (textColorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, textColorResourceId)
                view.tabTextColors = color
            } else {
                val color = skinResProcessor.getColorStateList(textColorResourceId)
                view.tabTextColors = color
            }
        }
    }

    fun setTabIndicatorColor(view: TabLayout, textColorResourceId: Int) {
        if (textColorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColor(view.context, textColorResourceId)
                view.setSelectedTabIndicatorColor(color)
            } else {
                val color = skinResProcessor.getColor(textColorResourceId)
                view.setSelectedTabIndicatorColor(color)
            }
        }
    }

    fun setColorSchemeColors(view: SwipeRefreshLayout, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColor(view.context, colorResourceId)
                view.setColorSchemeColors(color)
            } else {
                val color = skinResProcessor.getColor(colorResourceId)
                view.setColorSchemeColors(color)
            }
        }
    }

    fun setSrlPrimaryColorAttr(view: SmartRefreshLayout, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColor(view.context, colorResourceId)
                view.setPrimaryColors(color)
            } else {
                val color = skinResProcessor.getColor(colorResourceId)
                view.setPrimaryColors(color)
            }
        }
    }

    fun setImageViewTint(view: ImageView, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.imageTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.imageTintList = color
            }
        }
    }

    fun setColorSchemeColors(view: MaterialHeader, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColor(view.context, colorResourceId)
                view.setColorSchemeColors(color)
            } else {
                val color = skinResProcessor.getColor(colorResourceId)
                view.setColorSchemeColors(color)
            }
        }
    }

    fun setAnimatingColor(view: BallPulseFooter, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColor(view.context, colorResourceId)
                view.setAnimatingColor(color)
            } else {
                val color = skinResProcessor.getColor(colorResourceId)
                view.setAnimatingColor(color)
            }
        }
    }

    fun setTextColor(view: TextView, textColorResourceId: Int) {
        if (textColorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, textColorResourceId)
                view.setTextColor(color)
            } else {
                val color = skinResProcessor.getColorStateList(textColorResourceId)
                view.setTextColor(color)
            }
        }
    }

    fun setTextColorHint(view: TextView, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.setHintTextColor(color)
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.setHintTextColor(color)
            }
        }
    }

    fun setThumbTint(view: SwitchCompat, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.thumbTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.thumbTintList = color
            }
        }
    }

    fun setButtonTint(view: CompoundButton, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.buttonTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.buttonTintList = color
            }
        }
    }

    fun setTrackTint(view: SwitchCompat, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.trackTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.trackTintList = color
            }
        }
    }

    fun setIndeterminateTint(view: ProgressBar, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.indeterminateTintList = color
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.indeterminateTintList = color
            }
        }
    }

    fun setCardBackgroundColor(view: CardView, colorResourceId: Int) {
        if (colorResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                val color = ContextCompat.getColorStateList(view.context, colorResourceId)
                view.setCardBackgroundColor(color)
            } else {
                val color = skinResProcessor.getColorStateList(colorResourceId)
                view.setCardBackgroundColor(color)
            }
        }
    }

    fun setTypeface(view: TextView, textTypefaceResourceId: Int) {
        if (textTypefaceResourceId > 0) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.typeface = Typeface.DEFAULT
            } else {
                view.typeface = skinResProcessor.getTypeface(textTypefaceResourceId)
            }
        }
    }

    fun getTextColorResId(attrs: AttributeSet): Int {
        for (i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            val attrValue = attrs.getAttributeValue(i)
            if (attrName != "textColor") {
                continue
            }
            if (attrValue.startsWith("@")) {
                return attrValue.substring(1).toInt()
            }
        }
        return -1
    }

    fun getColor(resourceId: Int): Int {
        return SkinResourceProcessor.instance.getColor(resourceId)
    }

    fun getColorStateList(resourceId: Int): ColorStateList {
        return SkinResourceProcessor.instance.getColorStateList(resourceId)
    }

    fun getDrawableOrMipMap(resourceId: Int): Drawable {
        return SkinResourceProcessor.instance.getDrawableOrMipMap(resourceId)
    }

    fun getString(resourceId: Int): String {
        return SkinResourceProcessor.instance.getString(resourceId)
    }

    fun getSkinResourceId(resourceId: Int): Int {
        return SkinResourceProcessor.instance.getSkinResourceId(resourceId)
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    fun getBackgroundOrSrc(resourceId: Int): Any? {
        return SkinResourceProcessor.instance.getBackgroundOrSrc(resourceId)
    }

    // 获得字体
    fun getTypeface(resourceId: Int): Typeface {
        return SkinResourceProcessor.instance.getTypeface(resourceId)
    }

    fun setNavigation(activity: Activity, @ColorRes navigationBarColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.navigationBarColor = getColor(navigationBarColor)
        }
    }

    fun setActionBar(activity: AppCompatActivity, @ColorRes actionBarColor: Int) {
        val actionBar = activity.supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(getColor(actionBarColor)))
    }

    fun setColorStatusBar(window: Window, statusBarColor: Int, darkTextColor: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = statusBarColor //设置状态栏颜色
            if (darkTextColor)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}