package com.su.mediabox

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceDataStore
import com.su.mediabox.config.Const
import com.su.mediabox.util.JavaBoxClass
import com.su.mediabox.util.getRawClass
import com.su.mediabox.util.logD
import com.su.mediabox.util.unsafeLazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
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

private val keyMap: MutableMap<String, Preferences.Key<*>> by lazy { mutableMapOf() }

@Suppress("UNCHECKED_CAST")
inline fun <reified T> key(key: String): Preferences.Key<T> = key(key, T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T> key(key: String, typeClass: Class<T>): Preferences.Key<T> =
    (keyMap[key] ?: when (typeClass) {
        JavaBoxClass.Integer, Int::class.java -> intPreferencesKey(key)
        JavaBoxClass.Long, Long::class.java -> longPreferencesKey(key)
        JavaBoxClass.Float, Float::class.java -> floatPreferencesKey(key)
        JavaBoxClass.Double, Double::class.java -> doublePreferencesKey(key)
        String::class.java -> stringPreferencesKey(key)
        JavaBoxClass.Boolean, Boolean::class.java -> booleanPreferencesKey(key)
        else -> throw RuntimeException("不支持的类型：${typeClass.name}")
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

private val prefDataStoreCoroutineScope = CoroutineScope(Dispatchers.IO)

inline fun <reified T> keyFlow(key: String, defaultValue: T) = App.context
    .appDataStore.data
    .map {
        it[key(key)] ?: defaultValue
    }

class DataStoreStateFlowWrapper<T>(key: String, private val targetStateFlow: StateFlow<T>) :
    StateFlow<T> by targetStateFlow {

    @Suppress("UNCHECKED_CAST")
    val key by unsafeLazy {
        key(key, value.getRawClass() as Class<T>).also {
            logD("获取key", it.toString())
        }
    }

    fun saveData(value: T) = prefDataStoreCoroutineScope.launch(Dispatchers.IO) {
        App.context.appDataStore.edit {
            it[key] = value
        }
    }
}

private inline fun <reified T> lazyDataStoreStateFlow(key: String, defaultValue: T) = unsafeLazy {
    runBlocking {
        DataStoreStateFlowWrapper(
            key, keyFlow(key, defaultValue).stateIn(prefDataStoreCoroutineScope)
        )
    }
}

object Pref {
    val isProxyPluginRepo by lazyDataStoreStateFlow(Const.Setting.NET_REPO_PROXY, true)
    val isShowPlayerBottomProgressBar by lazyDataStoreStateFlow(
        Const.Setting.SHOW_PLAY_BOTTOM_BAR, false
    )
    val appLaunchCount by lazyDataStoreStateFlow(Const.Setting.APP_LAUNCH_COUNT, 0)
    val playDefaultCore by lazyDataStoreStateFlow(
        Const.Setting.PLAY_ACTION_DEFAULT_CORE,
        Exo2PlayerManager::class.java.name
    )
}