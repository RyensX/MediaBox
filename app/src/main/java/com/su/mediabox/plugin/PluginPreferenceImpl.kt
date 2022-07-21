package com.su.mediabox.plugin

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.su.mediabox.App
import com.su.mediabox.key
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.pluginapi.util.PluginPreference
import kotlinx.coroutines.CoroutineScope
import com.su.mediabox.util.getRawClass
import com.su.mediabox.util.unsafeLazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first

object PluginPreferenceImpl : PluginPreference {

    private const val PLUGIN_PREF_PREFIX = "plugin_pref_"
    private const val VISUAL_PLUGIN_PREF_PREFIX = "visual_$PLUGIN_PREF_PREFIX"

    private val pluginDataStoreCache by unsafeLazy { mutableMapOf<String, DataStore<Preferences>>() }
    fun getPluginDataStore(pluginInfo: PluginInfo? = null) =
        (pluginInfo ?: PluginManager.currentLaunchPlugin.value)?.run {
            pluginDataStoreCache[id] ?: PreferenceDataStoreFactory.create(
                migrations = listOf(),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            ) {
                App.context.preferencesDataStoreFile("$PLUGIN_PREF_PREFIX$id")
            }.also { pluginDataStoreCache[id] = it }
        }

    fun checkIsVisualPref(key: String) = key.contains(VISUAL_PLUGIN_PREF_PREFIX)
    fun getSimplifyKey(key: String) = key.replace(
        if (checkIsVisualPref(key)) VISUAL_PLUGIN_PREF_PREFIX else PLUGIN_PREF_PREFIX,
        ""
    )

    private fun getKey(key: String, isVisual: Boolean) =
        "${if (isVisual) VISUAL_PLUGIN_PREF_PREFIX else PLUGIN_PREF_PREFIX}$key"

    override suspend fun <T> get(
        key: String,
        defaultValue: T,
        isVisual: Boolean
    ): T =
        getPluginDataStore()?.run {
            defaultValue.getRawClass()?.let { type ->
                data.first()[key(getKey(key, isVisual), type, false)]
            }
        } ?: defaultValue

    override suspend fun <T> set(
        key: String,
        value: T,
        isVisual: Boolean
    ): Boolean {
        value.getRawClass()?.let { type ->
            getPluginDataStore()?.edit {
                it[key((getKey(key, isVisual)), type, false)] = value
            } ?: return false
            return true
        } ?: return false
    }

}