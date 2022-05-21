package com.su.mediabox.view.fragment.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.su.mediabox.*
import com.su.mediabox.config.Const
import com.su.mediabox.util.*
import com.su.mediabox.util.update.AppUpdateHelper
import com.su.mediabox.util.update.AppUpdateStatus
import com.su.mediabox.view.activity.LicenseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsPageFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DataStorePreference(fragment = this)
        preferenceScreen = preferenceScreen {

            preferenceCategory {

                titleRes(R.string.net_category_title)

                checkPreference {
                    key = Const.Setting.NET_REPO_PROXY
                    setIcon(R.drawable.ic_language_main_color_2_24_skin)
                    titleRes(R.string.net_proxy_title)
                    summaryRes(R.string.net_proxy_summary)
                    //TODO
                    isEnabled = false
                }
            }

            preferenceCategory {

                titleRes(R.string.player_category_title)

                switchPreference {
                    key = Const.Setting.SHOW_PLAY_BOTTOM_BAR
                    titleRes(R.string.player_bottom_progress_title)
                    summaryRes(R.string.player_bottom_progress_summary)

                    Pref.isShowPlayerBottomProgressBar.observe(this@SettingsPageFragment) {
                        isChecked = it
                    }
                }

                //TODO 实现单选Preference
                preference {
                    setIcon(R.drawable.ic_baseline_core_24)
                    titleRes(R.string.player_default_core_title)
                    summaryRes(R.string.player_default_core_summary)
                }
            }

            preferenceCategory {
                titleRes(R.string.about_category_title)

                preference {
                    summaryRes(R.string.version_summary)

                    AppUpdateHelper.instance.apply {
                        getUpdateStatus()
                            .observe(this@SettingsPageFragment) {
                                isEnabled = it != AppUpdateStatus.CHECKING
                                when (it) {
                                    AppUpdateStatus.VALID ->
                                        context.getString(R.string.app_no_update_hint).showToast()
                                    AppUpdateStatus.DATED -> noticeUpdate(requireActivity() as AppCompatActivity)
                                    else -> Unit
                                }
                            }
                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                        val packageInfo =
                            context.run { packageManager.getPackageInfo(packageName, 0) }
                        titleRes(
                            R.string.version_title,
                            packageInfo.versionName,
                            packageInfo.versionCode,
                            if (BuildConfig.DEBUG) "Debug" else "Release"
                        )

                        setOnPreferenceClickListener {
                            AppUpdateHelper.instance.checkUpdate()
                            true
                        }
                    }
                }

                preference {
                    setIcon(R.drawable.ic_github)
                    titleRes(R.string.open_source_title)
                    summary = Const.Common.GITHUB_URL
                    onPreferenceClickListener = this@SettingsPageFragment
                }

                preference {
                    titleRes(R.string.open_source_licenses)
                    summaryRes(R.string.open_source_licenses_summary, Const.Common.licenses.size)
                    setOnPreferenceClickListener {
                        context.goActivity<LicenseActivity>()
                        true
                    }
                }

                preference {
                    setIcon(R.drawable.ic_github_star)
                    titleRes(R.string.open_source_star)
                    onPreferenceClickListener = this@SettingsPageFragment
                }

                preference {
                    setIcon(R.drawable.ic_baseline_eye_24)
                    titleRes(R.string.open_source_watch)
                    onPreferenceClickListener = this@SettingsPageFragment
                }
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        Util.openBrowser(Const.Common.GITHUB_URL)
        return true
    }
}