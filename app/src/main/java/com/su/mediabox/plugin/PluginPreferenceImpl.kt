package com.su.mediabox.plugin

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.su.mediabox.App
import com.su.mediabox.key
import com.su.mediabox.model.PluginInfo
import com.su.mediabox.plugin.PluginPreferenceImpl.checkKeyExist
import com.su.mediabox.pluginapi.util.PluginPreference
import com.su.mediabox.util.appCoroutineScope
import kotlinx.coroutines.CoroutineScope
import com.su.mediabox.util.getRawClass
import com.su.mediabox.util.unsafeLazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    suspend fun <T> DataStore<Preferences>?.get(
        key: String,
        defaultValue: T,
        isVisual: Boolean
    ): T = defaultValue.getRawClass()?.let { type ->
        this?.data?.first()?.get(key(getKey(key, isVisual), type, false))
    } ?: defaultValue

    suspend fun <T> DataStore<Preferences>?.set(
        key: String,
        value: T,
        isVisual: Boolean
    ): Boolean {
        value.getRawClass()?.let { type ->
            this?.edit {
                it[key((getKey(key, isVisual)), type, false)] = value
            } ?: return false
            return true
        } ?: return false
    }

    suspend fun DataStore<Preferences>?.checkKeyExist(
        key: String,
        type: Class<*>,
        isVisual: Boolean
    ): Boolean {
        var result: Any? = null
        this?.edit {
            result = it[key(getKey(key, isVisual), type, false)]
        }
        return result != null
    }

    override suspend fun <T> get(
        key: String,
        defaultValue: T,
        isVisual: Boolean
    ): T = getPluginDataStore().get(key, defaultValue, isVisual)

    override suspend fun <T> set(
        key: String,
        value: T,
        isVisual: Boolean
    ): Boolean = getPluginDataStore().set(key, value, isVisual)

    override fun <T> initKey(key: String, defaultValue: T, isVisual: Boolean) {
        appCoroutineScope.launch {
            runCatching {
                getPluginDataStore()?.apply {
                    if (!checkKeyExist(key, defaultValue.getRawClass()!!, isVisual)) {
                        set(key, defaultValue, isVisual)
                    }
                }
            }
        }
    }

}