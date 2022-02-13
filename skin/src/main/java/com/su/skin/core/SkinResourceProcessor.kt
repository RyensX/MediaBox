package com.su.skin.core

import android.app.Application
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import com.su.skin.SkinManager
import java.lang.reflect.Method
import java.util.*


class SkinResourceProcessor private constructor(val application: Application) {
    private val appResources: Resources = application.resources
    private lateinit var skinResources: Resources
    private lateinit var skinPackageName: String
    lateinit var skinPath: String

    /*是否使用默认资源或app内资源，根据是否包含skinSuffix确定是否跟换资源*/
    private var usingDefaultSkin = true
    lateinit var skinSuffix: String
    private val skinCacheMap: MutableMap<String, SkinCache> = HashMap()

    fun usingDefaultSkin(): Boolean = usingDefaultSkin

    fun usingInnerAppSkin(): Boolean = skinSuffix.isBlank()

    fun getDarkMode(): Int {
        return SkinPreferenceUtil.getInt(
            application,
            SkinManager.KEY_SKIN_DARK,
            SkinManager.DARK_MODE_NO
        )
    }

    fun autoLoadSkinResources() {
        SkinPreferenceUtil
            .getInt(application, SkinManager.KEY_SKIN_DARK, SkinManager.DARK_MODE_NO).let {
                when (it) {
                    SkinManager.DARK_MODE_YES -> {
                        SkinManager.loadSkinResources("", SkinManager.KEY_SKIN_DARK_SUFFIX)
                    }
                    SkinManager.DARK_MODE_NO -> {
                        SkinManager.loadSkinResources(
                            SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_PATH)
                                ?: "",
                            SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_SUFFIX)
                                ?: ""
                        )
                    }
                    SkinManager.DARK_FOLLOW_SYSTEM -> {
                        val currentNightMode: Int =
                            application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                            SkinManager.loadSkinResources("", SkinManager.KEY_SKIN_DARK_SUFFIX)
                        } else {
                            SkinManager.loadSkinResources(
                                SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_PATH)
                                    ?: "",
                                SkinPreferenceUtil.getString(
                                    application,
                                    SkinManager.KEY_SKIN_SUFFIX
                                )
                                    ?: ""
                            )
                        }
                    }
                }
            }
    }

    fun systemDarkModeChanged(newConfig: Configuration) {
        if (SkinPreferenceUtil.getInt(
                application,
                SkinManager.KEY_SKIN_DARK,
                SkinManager.DARK_MODE_NO
            ) == SkinManager.DARK_FOLLOW_SYSTEM
        ) {
            val currentNightMode: Int = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                SkinManager.notifyListener("", SkinManager.KEY_SKIN_DARK_SUFFIX)
            } else if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                SkinManager.notifyListener(
                    SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_PATH)
                        ?: "",
                    SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_SUFFIX)
                        ?: ""
                )
            }
        }
    }

    fun setDarkMode(b: Int) {
        when (b) {
            SkinManager.DARK_MODE_YES -> {
                SkinPreferenceUtil.putInt(
                    application, SkinManager.KEY_SKIN_DARK, SkinManager.DARK_MODE_YES
                )
                SkinManager.notifyListener("", SkinManager.KEY_SKIN_DARK_SUFFIX)
            }
            SkinManager.DARK_MODE_NO -> {
                SkinPreferenceUtil.putInt(
                    application, SkinManager.KEY_SKIN_DARK, SkinManager.DARK_MODE_NO
                )
                SkinManager.notifyListener(
                    SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_PATH) ?: "",
                    SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_SUFFIX) ?: ""
                )
            }
            SkinManager.DARK_FOLLOW_SYSTEM -> {
                SkinPreferenceUtil.putInt(
                    application, SkinManager.KEY_SKIN_DARK, SkinManager.DARK_FOLLOW_SYSTEM
                )
                val currentNightMode: Int =
                    application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    SkinManager.notifyListener("", SkinManager.KEY_SKIN_DARK_SUFFIX)
                } else if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                    SkinManager.notifyListener(
                        SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_PATH) ?: "",
                        SkinPreferenceUtil.getString(application, SkinManager.KEY_SKIN_SUFFIX) ?: ""
                    )
                }
            }
        }
    }

    // 设置深色模式请不要直接调用此方法，请使用setDarkMode方法
    fun loadSkinResources(skinPath: String, skinSuffix: String) {
        this.skinSuffix = skinSuffix
        this.skinPath = skinPath
        if (skinPath.isBlank()) {
            usingDefaultSkin = true
            return
        }
        if (skinCacheMap.containsKey(skinPath)) {
            usingDefaultSkin = false
            val skinCache = skinCacheMap[skinPath]
            if (skinCache == null) {
                usingDefaultSkin = true
                return
            }
            skinResources = skinCache.skinResource
            skinPackageName = skinCache.skinPackageName
            return
        }
        try {
            val assetManager = AssetManager::class.java.newInstance()
            if (addAssetsPath == null) {
                assetManager.javaClass.getDeclaredMethod(
                    METHOD_ADD_ASSETS_PATH,
                    String::class.java
                ).apply {
                    isAccessible = true
                    addAssetsPath = this
                }
            }
            addAssetsPath?.invoke(assetManager, skinPath)
            skinResources = Resources(
                assetManager,
                appResources.displayMetrics,
                appResources.configuration
            )
            val packageInfo = application.packageManager
                .getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES)
            skinPackageName = packageInfo!!.packageName
            usingDefaultSkin = skinPackageName.isBlank()
            if (!usingDefaultSkin) {
                skinCacheMap[skinPath] = SkinCache(skinResources, skinPackageName, skinSuffix)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            usingDefaultSkin = true
        }
    }

    fun getSkinResourceId(resourceId: Int): Int {
        val resourceName = appResources.getResourceEntryName(resourceId)
        val resourceType = appResources.getResourceTypeName(resourceId)
        val skinResourceName = resourceName + skinSuffix
        if (skinPath.isBlank()) {
            if (skinSuffix.isNotBlank()) {
                val skinResourceId = appResources.getIdentifier(
                    skinResourceName,
                    resourceType,
                    application.packageName
                )
                return if (skinResourceId == 0) resourceId else skinResourceId
            }
            return resourceId
        }
        val skinResourceId =
            skinResources.getIdentifier(skinResourceName, resourceType, skinPackageName)
        usingDefaultSkin = skinResourceId == 0
        if (usingDefaultSkin) {
            Log.i(TAG, "skin res:$skinResourceName not found,use default res:$resourceName")
        }
        return if (usingDefaultSkin) resourceId else skinResourceId
    }

    fun getSkinThemeId(themeId: Int): Int {
        val resourceName = appResources.getResourceEntryName(themeId)
        val resourceType = appResources.getResourceTypeName(themeId)
        val skinResourceName = resourceName +
                if (skinSuffix.startsWith("_")) skinSuffix.replaceFirst("_", ".")
                else skinSuffix
        if (skinPath.isBlank()) {
            if (skinSuffix.isNotBlank()) {
                val skinResourceId = appResources.getIdentifier(
                    skinResourceName,
                    resourceType,
                    application.packageName
                )
                return if (skinResourceId == 0) themeId else skinResourceId
            }
            return themeId
        }
        val skinResourceId =
            skinResources.getIdentifier(skinResourceName, resourceType, skinPackageName)
        usingDefaultSkin = skinResourceId == 0
        if (usingDefaultSkin) {
            Log.i(TAG, "skin res:$skinResourceName not found,use default res:$resourceName")
        }
        return if (usingDefaultSkin) themeId else skinResourceId
    }

    fun getColor(resourceId: Int): Int {
        val resId = getSkinResourceId(resourceId)
        return if (usingDefaultSkin) appResources.getColor(resId) else skinResources.getColor(resId)
    }

    fun getColorStateList(resourceId: Int): ColorStateList {
        val resId = getSkinResourceId(resourceId)
        return if (usingDefaultSkin) appResources.getColorStateList(resId)
        else skinResources.getColorStateList(resId)
    }

    fun getDrawableOrMipMap(resourceId: Int): Drawable {
        val resId = getSkinResourceId(resourceId)
        return if (usingDefaultSkin) appResources.getDrawable(resId, application.theme)
        else skinResources.getDrawable(resId, application.theme)
    }

    fun getString(resourceId: Int): String {
        val resId = getSkinResourceId(resourceId)
        return if (usingDefaultSkin) appResources.getString(resId)
        else skinResources.getString(resId)
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    fun getBackgroundOrSrc(resourceId: Int): Any? {
        // 需要获取当前属性的类型名Resources.getResourceTypeName(resourceId)再判断
        when (appResources.getResourceTypeName(resourceId)) {
            "color" -> return getColor(resourceId)
            "mipmap", "drawable" -> return getDrawableOrMipMap(resourceId)
        }
        return null
    }

    // 获得字体
    fun getTypeface(resourceId: Int): Typeface {
        // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        val skinTypefacePath = getString(resourceId)
        // 路径为空，使用系统默认字体
        if (skinTypefacePath.isBlank()) return Typeface.DEFAULT
        return if (usingDefaultSkin) Typeface.createFromAsset(appResources.assets, skinTypefacePath)
        else Typeface.createFromAsset(skinResources.assets, skinTypefacePath)
    }

    companion object {
        private val TAG = SkinResourceProcessor::class.java.simpleName

        //todo 反射：这里用的反射加载外部资源
        private const val METHOD_ADD_ASSETS_PATH = "addAssetPath"
        private var addAssetsPath: Method? = null
        lateinit var instance: SkinResourceProcessor

        fun init(application: Application) {
            if (!this::instance.isInitialized) {
                synchronized(SkinManager::class.java) {
                    if (!this::instance.isInitialized) {
                        instance = SkinResourceProcessor(application)
                    }
                }
            }
        }

        fun isInitialized(): Boolean = this::instance.isInitialized
    }
}