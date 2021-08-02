package com.skyd.skin.core

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import com.skyd.skin.SkinManager
import com.skyd.skin.core.listeners.ChangeSkinListener


open class SkinBaseActivity : AppCompatActivity(), ChangeSkinListener {
    private lateinit var inflater: LayoutInflater
    protected open var statusBarSkin: Boolean = true     // 是否对状态栏换肤

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isChangeSkin()) {
            SkinManager.init(application)
            inflater = LayoutInflater.from(this)
            LayoutInflaterCompat.setFactory2(inflater, this)
            SkinManager.addListener(this)
            SkinManager.autoLoadSkinResources()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return if (isChangeSkin())
            SkinManager.onCreateView(this, inflater, parent, name, context, attrs)
        else super.onCreateView(parent, name, context, attrs)
    }

    /**
     * @return 是否需要开启换肤，默认是true，false表示开启
     */
    protected open fun isChangeSkin(): Boolean {
        return true
    }

    protected fun defaultSkin() {
        SkinManager.notifyListener("", "")
    }

    protected fun dynamicSkin(skinSuffix: String) {
        SkinManager.notifyListener("", skinSuffix)
    }

    protected fun dynamicSkin(skinPath: String, skinSuffix: String) {
        SkinManager.notifyListener(skinPath, skinSuffix)
    }

    override fun onChangeSkin() {
        if (statusBarSkin) onChangeStatusBarSkin()
        SkinManager.applyViews(window.decorView)
    }

    protected open fun onChangeStatusBarSkin() {}

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.removeListener(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        SkinManager.systemDarkModeChanged(newConfig)
    }
}