package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.fragment.EverydayAnimeFragment
import com.skyd.imomoe.view.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    private var backPressTime = 0L
    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }
    private var homeFragment: HomeFragment? = null
    private var everydayAnimeFragment: EverydayAnimeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            homeFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                HOME_FRAGMENT_KEY
            ) as HomeFragment?
            everydayAnimeFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                EVERYDAY_ANIME_FRAGMENT_KEY
            ) as EverydayAnimeFragment?
        }

        cl_home_button.setOnClickListener {
            setTabSelection(0)
        }

        cl_everyday_anime_button.setOnClickListener {
            setTabSelection(1)
        }

        setTabSelection(0)
    }

    private fun setTabSelection(index: Int) {
        clearAllSelected()
        fragmentManager.beginTransaction().apply {
            hideFragments(this)
            when (index) {
                0 -> {
                    iv_control_bar_home.isSelected = true
                    tv_control_bar_home.isSelected = true
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
                    iv_control_bar_everyday_anime.isSelected = true
                    tv_control_bar_everyday_anime.isSelected = true
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
                else -> {
                    iv_control_bar_home.isSelected = true
                    tv_control_bar_home.isSelected = true
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
        }.commitAllowingStateLoss()
    }

    private fun clearAllSelected() {
        iv_control_bar_home.isSelected = false
        tv_control_bar_home.isSelected = false
        iv_control_bar_everyday_anime.isSelected = false
        tv_control_bar_everyday_anime.isSelected = false
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        transaction.run {
            homeFragment?.run {
                hide(this)
            }
            everydayAnimeFragment?.run {
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
        super.onSaveInstanceState(outState)

    }

    companion object {
        private const val HOME_FRAGMENT_KEY = "homeFragment"
        private const val EVERYDAY_ANIME_FRAGMENT_KEY = "EverydayAnimeFragment"
    }
}