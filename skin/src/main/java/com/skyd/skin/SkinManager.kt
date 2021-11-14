package com.skyd.skin

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
import androidx.annotation.AttrRes
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        android.R.attr.progressDrawable,
        R.attr.menu,
        R.attr.contentScrim
    ).apply { sort() }      // 需要升序排序，所以要调用sort()

    // 在app中自定义
    private var customAttrIds: HashMap<Int, CustomSetSkinTagListener> = HashMap()     // 需要升序排序

    //todo 反射：这里使用反射创建View
    private val sConstructorMap: MutableMap<String, Constructor<out View>> = HashMap()
    private val mConstructorArgs = arrayOfNulls<Any>(2)
    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sClassPrefixList = arrayOf("android.widget.", "android.view.", "android.webkit.")

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
                sClassPrefixList.forEach {
                    view = LayoutInflater.from(context).createView(name, it, attrs)
                    if (view != null) return@forEach
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG && e !is ClassNotFoundException) e.printStackTrace()
            if (-1 == name.indexOf('.')) {
                sClassPrefixList.forEach {
                    view = createView(context, name, it)
                    if (view != null) return@forEach
                }
            } else {
                view = createView(context, name, null)
            }
            if (view == null) {
                mConstructorArgs[0] = null
                mConstructorArgs[1] = null
            }
        }
        return view
    }

    private fun createView(context: Context, name: String, prefix: String?): View? {
        try {
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
        } catch (e: ClassNotFoundException) {
            return null
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            return null
        }
    }

    fun addCustomAttrIds(@AttrRes attrId: Int, listener: CustomSetSkinTagListener) {
        customAttrIds[attrId] = listener
    }

    /**
     * 多用于动态new的View（手动创建的View）的换肤
     */
    fun setViewTag(view: View) {
        val attrsSet = view.getTag(R.id.change_skin_tag)
        val tag: SkinAttrsSet = if (attrsSet is SkinAttrsSet) attrsSet
        else SkinAttrsSet()
        view.setTag(R.id.change_skin_tag, tag)
        if (view is ChangeCustomSkinListener) {
            view.setCustomTag()
        }
    }

    /**
     * 设置自定义View的自定义属性（没在xml里应用的）
     */
    fun setCustomViewAttrs(view: View, vararg skinAttr: SkinAttr) {
        val temp = view.getTag(R.id.change_skin_tag)
        val tag: SkinAttrsSet = if (temp is SkinAttrsSet) temp
        else SkinAttrsSet()
        skinAttr.forEach {
            tag.attrsMap[it.tag()] = it
        }
        view.setTag(R.id.change_skin_tag, tag)
    }

    /**
     * 设置自定义View的自定义属性（没在xml里应用的）
     */
    fun setCustomViewAttrs(view: View, skinAttrsSet: SkinAttrsSet) {
        val temp = view.getTag(R.id.change_skin_tag)
        val tag: SkinAttrsSet = if (temp is SkinAttrsSet) temp
        else SkinAttrsSet()
        tag.attrsMap.putAll(skinAttrsSet.attrsMap)
    }

    /**
     * 获取自定义View的自定义属性（没在xml里应用的）
     */
    fun getCustomViewAttrs(view: View): SkinAttrsSet? {
        val attrsSet = view.getTag(R.id.change_skin_tag)
        if (attrsSet is SkinAttrsSet) {
            return attrsSet
        }
        return null
    }

    private fun setSkinTag(context: Context, attrs: AttributeSet, view: View) {
        val typedArray = context.obtainStyledAttributes(attrs, attrIds)
        val skinAttrsSet = SkinAttrsSet()
        val defValue = -1
        attrIds.forEachIndexed { index, i ->
            var resId = typedArray.getResourceId(index, defValue)
            val attr = when (i) {
                R.attr.background, android.R.attr.background -> BackgroundAttr()
                android.R.attr.src -> SrcAttr()
                android.R.attr.textColor -> {
                    if (resId == -1 && view is TextView) {
                        resId = getTextColorResId(attrs)
                    }
                    TextColorAttr()
                }
                android.R.attr.drawableStart -> DrawableStartAttr()
                android.R.attr.drawableEnd -> DrawableEndAttr()
                R.attr.tabIndicatorColor -> TabIndicatorColorAttr()
                R.attr.tabTextColor -> {
                    if (resId == -1 && view is TabLayout) {
                        resId = getTextColorResId(attrs)
                    }
                    TabTextColorAttr()
                }
                android.R.attr.scrollbarThumbVertical -> ScrollbarThumbVerticalAttr()
                R.attr.tint -> ImageViewTintAttr()
                android.R.attr.backgroundTint -> BackgroundTintAttr()
                android.R.attr.textColorHint -> TextColorHintAttr()
                R.attr.drawableTopCompat -> DrawableTopCompatAttr()
                R.attr.colorPrimary -> ColorPrimaryAttr()
                R.attr.srlPrimaryColor -> SrlPrimaryColorAttr()
                R.attr.thumbTint -> ThumbTintAttr()
                R.attr.trackTint -> TrackTintAttr()
                android.R.attr.buttonTint -> ButtonTintAttr()
                R.attr.cardBackgroundColor -> CardBackgroundColorAttr()
                android.R.attr.indeterminateTint -> IndeterminateTintAttr()
                android.R.attr.thumb -> ThumbAttr()
                android.R.attr.progressDrawable -> ProgressDrawableAttr()
                R.attr.contentScrim -> ContentScrimAttr()
                R.attr.menu -> null
                else -> null
            }
            if (attr != null && resId != -1) {
                attr.attrResourceRefId = resId
                skinAttrsSet.attrsMap[attr.tag()] = attr
            }
        }
        customAttrIds.toSortedMap().onEachIndexed { index, entry ->
            val resId = typedArray.getResourceId(index, defValue)
            entry.value.setSkinTag(entry.key, resId)?.apply {
                skinAttrsSet.attrsMap[first] = second
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

    interface CustomSetSkinTagListener {
        fun setSkinTag(attrId: Int, resId: Int): Pair<String, SkinAttr>?
    }
}