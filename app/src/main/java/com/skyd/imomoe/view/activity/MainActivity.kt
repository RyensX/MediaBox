package com.skyd.imomoe.view.activity

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const.ShortCuts.Companion.ACTION_EVERYDAY
import com.skyd.imomoe.config.Const.ShortCuts.Companion.ID_DOWNLOAD
import com.skyd.imomoe.config.Const.ShortCuts.Companion.ID_EVERYDAY
import com.skyd.imomoe.config.Const.ShortCuts.Companion.ID_FAVORITE
import com.skyd.imomoe.databinding.ActivityMainBinding
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.clickScale
import com.skyd.imomoe.util.eventbus.RefreshEvent
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.view.fragment.EverydayAnimeFragment
import com.skyd.imomoe.view.fragment.HomeFragment
import com.skyd.imomoe.view.fragment.MoreFragment
import org.greenrobot.eventbus.EventBus


class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var selectedTab = -1
    private var backPressTime = 0L
    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }
    private var homeFragment: HomeFragment? = null
    private var everydayAnimeFragment: EverydayAnimeFragment? = null
    private var moreFragment: MoreFragment? = null
    private lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        action = intent.action ?: ""

        //检查更新
        val appUpdateHelper = AppUpdateHelper.instance
        appUpdateHelper.getUpdateStatus().observe(this, Observer {
            when (it) {
                AppUpdateStatus.UNCHECK -> appUpdateHelper.checkUpdate()
                AppUpdateStatus.DATED -> appUpdateHelper.noticeUpdate(this)
                AppUpdateStatus.TO_BE_INSTALLED -> appUpdateHelper.installUpdate(this)
                else -> Unit
            }
        })

        if (savedInstanceState != null) {
            homeFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                HOME_FRAGMENT_KEY
            ) as HomeFragment?
            everydayAnimeFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                EVERYDAY_ANIME_FRAGMENT_KEY
            ) as EverydayAnimeFragment?
            moreFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                MORE_FRAGMENT_KEY
            ) as MoreFragment?
            setTabSelection(savedInstanceState.getInt(SELECTED_TAB))
        } else {
            if (action == ACTION_EVERYDAY) setTabSelection(1)
            else setTabSelection(0)
        }

        mBinding.run {
            clHomeButton.setOnClickListener {
                it.clickScale(0.8f)
                setTabSelection(0)
            }

            clEverydayAnimeButton.setOnClickListener {
                it.clickScale(0.8f)
                setTabSelection(1)
            }

            clMoreButton.setOnClickListener {
                it.clickScale(0.8f)
                setTabSelection(2)
            }
        }

        registerShortcuts()
    }

    /**
     * 设置app图标快捷菜单
     */
    private fun registerShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val mShortcutManager = getSystemService(ShortcutManager::class.java)
            val shortcutInfoList = listOf(
                ShortcutInfo.Builder(this, ID_FAVORITE)
                    .setShortLabel(getString(R.string.shortcuts_favorite_short))
                    .setLongLabel(getString(R.string.shortcuts_favorite_long))
                    .setIcon(
                        Icon.createWithResource(this, R.drawable.layerlist_shortcuts_favorite_24)
                    )
                    .setIntent(
                        Intent(this, FavoriteActivity::class.java).setAction(Intent.ACTION_VIEW)
                    )
                    .build(),
                ShortcutInfo.Builder(this, ID_EVERYDAY)
                    .setShortLabel(getString(R.string.shortcuts_everyday_short))
                    .setLongLabel(getString(R.string.shortcuts_everyday_long))
                    .setIcon(
                        Icon.createWithResource(this, R.drawable.layerlist_shortcuts_everyday_24)
                    )
                    .setIntent(
                        Intent(this, MainActivity::class.java).setAction(ACTION_EVERYDAY)
                    )
                    .build(),
                ShortcutInfo.Builder(this, ID_DOWNLOAD)
                    .setShortLabel(getString(R.string.shortcuts_download_short))
                    .setLongLabel(getString(R.string.shortcuts_download_long))
                    .setIcon(
                        Icon.createWithResource(this, R.drawable.layerlist_shortcuts_download_24)
                    )
                    .setIntent(
                        Intent(this, AnimeDownloadActivity::class.java)
                            .setAction(Intent.ACTION_VIEW)
                    )
                    .build()
            )
            mShortcutManager.dynamicShortcuts = shortcutInfoList
        }
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun setTabSelection(index: Int) {
        // 如果已经选中了，则刷新
        if (selectedTab == index) {
            EventBus.getDefault().post(RefreshEvent())
            return
        }
        clearAllSelected()
        fragmentManager.beginTransaction().apply {
            hideFragments(this)
            when (index) {
                0 -> {
                    mBinding.ivControlBarHome.isSelected = true
                    mBinding.tvControlBarHome.isSelected = true
                    if (homeFragment == null) {
                        val fragment = HomeFragment()
                        homeFragment = fragment
                        add(R.id.fl_main_activity_fragment_container, fragment)
                    } else {
                        homeFragment?.run {
                            show(this)
                        }
                    }
                }
                1 -> {
                    mBinding.ivControlBarEverydayAnime.isSelected = true
                    mBinding.tvControlBarEverydayAnime.isSelected = true
                    if (everydayAnimeFragment == null) {
                        val fragment = EverydayAnimeFragment()
                        everydayAnimeFragment = fragment
                        add(R.id.fl_main_activity_fragment_container, fragment)
                    } else {
                        everydayAnimeFragment?.run {
                            show(this)
                        }
                    }
                }
                2 -> {
                    mBinding.ivControlBarMore.isSelected = true
                    mBinding.tvControlBarMore.isSelected = true
                    if (moreFragment == null) {
                        val fragment = MoreFragment()
                        moreFragment = fragment
                        add(R.id.fl_main_activity_fragment_container, fragment)
                    } else {
                        moreFragment?.run {
                            show(this)
                        }
                    }
                }
                else -> {
                    mBinding.ivControlBarHome.isSelected = true
                    mBinding.tvControlBarHome.isSelected = true
                    if (homeFragment == null) {
                        val fragment = HomeFragment()
                        homeFragment = fragment
                        add(R.id.fl_main_activity_fragment_container, fragment)
                    } else {
                        homeFragment?.run {
                            show(this)
                        }
                    }
                }
            }
            selectedTab = index
        }.commitAllowingStateLoss()
    }

    private fun clearAllSelected() {
        mBinding.run {
            ivControlBarHome.isSelected = false
            tvControlBarHome.isSelected = false
            ivControlBarEverydayAnime.isSelected = false
            tvControlBarEverydayAnime.isSelected = false
            ivControlBarMore.isSelected = false
            tvControlBarMore.isSelected = false
        }
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        transaction.run {
            homeFragment?.run {
                hide(this)
            }
            everydayAnimeFragment?.run {
                hide(this)
            }
            moreFragment?.run {
                hide(this)
            }
        }
    }

    private fun processBackPressed() {
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000) {
            String.format(
                App.context.getString(R.string.press_again_to_exit),
                App.context.getString(App.context.applicationInfo.labelRes)
            ).showToast()
            backPressTime = now
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            processBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        homeFragment?.let {
            supportFragmentManager.putFragment(outState, HOME_FRAGMENT_KEY, it)
        }
        everydayAnimeFragment?.let {
            supportFragmentManager.putFragment(outState, EVERYDAY_ANIME_FRAGMENT_KEY, it)
        }
        moreFragment?.let {
            supportFragmentManager.putFragment(outState, MORE_FRAGMENT_KEY, it)
        }
        outState.putInt(SELECTED_TAB, selectedTab)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val HOME_FRAGMENT_KEY = "homeFragment"
        private const val EVERYDAY_ANIME_FRAGMENT_KEY = "everydayAnimeFragment"
        private const val MORE_FRAGMENT_KEY = "moreFragment"
        private const val SELECTED_TAB = "selectedTab"
    }
}
