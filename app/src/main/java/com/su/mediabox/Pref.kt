package com.su.mediabox

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceDataStore
import com.su.mediabox.config.Const
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException

class DataStorePreference(
    private val dataStore: DataStore<Preferences> = App.context.appDataStore,
    val fragment: Fragment
) :
    PreferenceDataStore() {

    private inline fun <reified T> getData(key: String, defValue: T) =
        runBlocking { dataStore.data.first()[key(key)] } ?: defValue

    override fun putString(key: String, value: String?) =
        fragment.lifecycleScope.saveData(key, value)

    override fun putInt(key: String, value: Int) = fragment.lifecycleScope.saveData(key, value)
    override fun putLong(key: String, value: Long) = fragment.lifecycleScope.saveData(key, value)
    override fun putFloat(key: String, value: Float) = fragment.lifecycleScope.saveData(key, value)
    override fun putBoolean(key: String, value: Boolean) =
        fragment.lifecycleScope.saveData(key, value)

    override fun getString(key: String, defValue: String?) = getData(key, defValue)
    override fun getInt(key: String, defValue: Int) = getData(key, defValue)
    override fun getLong(key: String, defValue: Long) = getData(key, defValue)
    override fun getFloat(key: String, defValue: Float) = getData(key, defValue)
    override fun getBoolean(key: String, defValue: Boolean) = getData(key, defValue)
}

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "${App.context.packageName}_pref")

val keyMap: MutableMap<String, Preferences.Key<*>> by lazy { mutableMapOf() }

@Suppress("UNCHECKED_CAST")
inline fun <reified T> key(key: String): Preferences.Key<T> =
    (keyMap[key] ?: when (T::class) {
        Int::class -> intPreferencesKey(key)
        Long::class -> longPreferencesKey(key)
        Float::class -> floatPreferencesKey(key)
        Double::class -> doublePreferencesKey(key)
        String::class -> stringPreferencesKey(key)
        Boolean::class -> booleanPreferencesKey(key)
        else -> throw RuntimeException("不支持的类型")
    }.also { keyMap[key] = it }) as Preferences.Key<T>

inline fun <reified T> CoroutineScope.saveData(key: String, value: T) {
    launch(Dispatchers.IO) {
        App.context.appDataStore.edit {
            it[key(key)] = value
        }
    }
}

fun CoroutineScope.updateData(transform: suspend Preferences.() -> Unit) {
    launch(Dispatchers.IO) {
        App.context.appDataStore.updateData {
            it.toMutablePreferences().apply {
                transform()
            }
        }
    }
}

inline fun <reified T> keyFlow(key: String, defaultValue: T) = App.context.appDataStore.data
    .map {
        it[key(key)] ?: defaultValue
    }

private inline fun <reified T> lazyKeyLiveData(key: String, defaultValue: T) =
    lazy(LazyThreadSafetyMode.NONE) {
        keyFlow(key, defaultValue).asLiveData().apply {
            //一直保持激活方便外部直接读取值
            observeForever {}
        }
    }

object Pref {
    val isShowPlayerBottomProgressBar by lazyKeyLiveData(Const.Setting.SHOW_PLAY_BOTTOM_BAR, false)
}