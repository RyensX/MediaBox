package com.su.mediabox.view.fragment

import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.su.mediabox.DataStorePreference
import com.su.mediabox.R
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.key
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.plugin.PluginPreferenceImpl
import com.su.mediabox.util.*
import com.su.mediabox.view.dialog.PluginManageBottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PluginPrefVisualFragment private constructor() : PreferenceFragmentCompat(),
    OnPreferenceLongClickListener {

    companion object {
        fun create(packageName: String) =
            PluginPrefVisualFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        PluginManageBottomSheetDialogFragment.PLUGIN_PACKAGE_NAME,
                        packageName
                    )
                }
            }
    }

    private val pluginInfo by unsafeLazy {
        arguments?.getString(PluginManageBottomSheetDialogFragment.PLUGIN_PACKAGE_NAME)
            ?.let { PluginManager.queryPluginInfo(it) }
    }

    private var dataStore: DataStore<Preferences>? = null

    private fun createPreference(entry: Map.Entry<Preferences.Key<*>, Any?>): Preference? =
        when (entry.value.getRawClass()) {
            //JavaBoxClass.Integer, Int::class.java,
            //JavaBoxClass.Long, Long::class.java,
            //JavaBoxClass.Float, Float::class.java,
            //JavaBoxClass.Double, Double::class.java,
            //TODO 这里实际上只能String，需要重新实现对应Preference。因此暂时只支持Boolean和String
            //TODO 左右KV的Preference
            String::class.java ->
                EditTextPreferenceLongClickWrapper(requireContext(), this)
            JavaBoxClass.Boolean, Boolean::class.java ->
                SwitchPreferenceLongClickWrapper(requireContext(), this)
            else -> null
        }?.apply {
            key = entry.key.name
            title = PluginPreferenceImpl.getSimplifyKey(entry.key.name)
            isIconSpaceReserved = false
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        PluginPreferenceImpl.getPluginDataStore(pluginInfo)?.apply {
            dataStore = this
            logD("创建持久可视化", pluginInfo?.id ?: "null")
            preferenceManager.preferenceDataStore = DataStorePreference(this, appCoroutineScope)
            val screen = preferenceManager.createPreferenceScreen(requireContext())
            preferenceScreen = screen
            lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
                data.first().also { preferences ->
                    preferences.asMap().forEach { entry ->
                        if (PluginPreferenceImpl.checkIsVisualPref(entry.key.name))
                            createPreference(entry)?.also {
                                screen.addPreference(it)
                            }
                    }
                }
            }
        }
    }

    override fun onPreferenceLongClick(preference: Preference): Boolean {
        if (preference is PreferenceBindView)
            preference.bindView?.also { bindView ->
                PopupMenu(requireContext(), bindView).apply {
                    menu.add(R.string.delete)
                    setOnMenuItemClickListener {
                        appCoroutineScope.launch {
                            dataStore?.edit { mp ->
                                dataStore?.data?.first()?.asMap()?.forEach {
                                    if (it.key.name == preference.key) {
                                        mp.remove(it.key)
                                        launch(Dispatchers.Main) {
                                            preferenceScreen.removePreference(
                                                preference
                                            )
                                        }
                                        return@edit
                                    }
                                }
                            }
                        }
                        true
                    }
                    show()
                }
            }
        return true
    }
}