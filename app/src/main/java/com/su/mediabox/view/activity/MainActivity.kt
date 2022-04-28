package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.config.Const.ShortCuts.Companion.ACTION_EVERYDAY
import com.su.mediabox.databinding.ActivityMainBinding
import com.su.mediabox.util.clickScale
import com.su.mediabox.util.eventbus.EventBusSubscriber
import com.su.mediabox.util.eventbus.MessageEvent
import com.su.mediabox.util.eventbus.RefreshEvent
import com.su.mediabox.util.eventbus.SelectHomeTabEvent
import com.su.mediabox.util.showToast
import com.su.mediabox.view.fragment.EverydayAnimeFragment
import com.su.mediabox.view.fragment.HomeFragment
import com.su.mediabox.view.fragment.MoreFragment
import com.umeng.message.PushAgent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Deprecated("更新2.0后删除")
class MainActivity : BasePluginActivity<ActivityMainBinding>(), EventBusSubscriber {
    private var selectedTab = -1
    private var backPressTime = 0L
    private var homeFragment: HomeFragment? = null
    private var everydayAnimeFragment: EverydayAnimeFragment? = null
    private var moreFragment: MoreFragment? = null
    private lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        action = intent.action ?: ""

        PushAgent.getInstance(this).onAppStart()

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
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun setTabSelection(index: Int) {
        // 如果已经选中了，则刷新
        if (selectedTab == index) {
            EventBus.getDefault().post(RefreshEvent())
            return
        }
        clearAllSelected()
        supportFragmentManager.beginTransaction().apply {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is SelectHomeTabEvent -> {
                setTabSelection(0)
            }
        }
    }

    companion object {
        private const val HOME_FRAGMENT_KEY = "homeFragment"
        private const val EVERYDAY_ANIME_FRAGMENT_KEY = "everydayAnimeFragment"
        private const val MORE_FRAGMENT_KEY = "moreFragment"
        private const val SELECTED_TAB = "selectedTab"
    }
}
