package com.su.mediabox

import android.content.Context
import android.graphics.Color
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
import com.su.mediabox.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.lang.RuntimeException

class DataStorePreference(
    private val dataStore: DataStore<Preferences> = App.context.appDataStore,
    private val prefCoroutineScope: CoroutineScope
) :
    PreferenceDataStore() {

    private fun edit(block: suspend (MutablePreferences) -> Unit) {
        prefCoroutineScope.launch { dataStore.edit(block) }
    }

    private inline fun <reified T> getData(key: String, defValue: T) =
        runBlocking { dataStore.data.first()[key(key)] } ?: defValue

    override fun putString(key: String, value: String?) =
        edit { it[stringPreferencesKey(key)] = value ?: "" }

    override fun putInt(key: String, value: Int) = edit { it[intPreferencesKey(key)] = value }
    override fun putLong(key: String, value: Long) = edit { it[longPreferencesKey(key)] = value }
    override fun putFloat(key: String, value: Float) = edit { it[floatPreferencesKey(key)] = value }
    override fun putBoolean(key: String, value: Boolean) =
        edit { it[booleanPreferencesKey(key)] = value }

    override fun getString(key: String, defValue: String?) = getData(key, defValue)
    override fun getInt(key: String, defValue: Int) = getData(key, defValue)
    override fun getLong(key: String, defValue: Long) = getData(key, defValue)
    override fun getFloat(key: String, defValue: Float) = getData(key, defValue)
    override fun getBoolean(key: String, defValue: Boolean) = getData(key, defValue)
}

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "${App.context.packageName}_pref")

private val keyMap: MutableMap<String, Preferences.Key<*>> by lazy { mutableMapOf() }

@Suppress("UNCHECKED_CAST")
inline fun <reified T> key(key: String, isCache: Boolean = true): Preferences.Key<T> =
    key(key, T::class.java, isCache)

@Suppress("UNCHECKED_CAST")
fun <T> key(key: String, typeClass: Class<T>, isCache: Boolean = true): Preferences.Key<T> =
    (keyMap[key] ?: when (typeClass) {
        JavaBoxClass.Integer, Int::class.java -> intPreferencesKey(key)
        JavaBoxClass.Long, Long::class.java -> longPreferencesKey(key)
        JavaBoxClass.Float, Float::class.java -> floatPreferencesKey(key)
        JavaBoxClass.Double, Double::class.java -> doublePreferencesKey(key)
        String::class.java -> stringPreferencesKey(key)
        JavaBoxClass.Boolean, Boolean::class.java -> booleanPreferencesKey(key)
        else -> throw RuntimeException("不支持的类型：${typeClass.name}")
    }.also {
        if (isCache)
            keyMap[key] = it
    }) as Preferences.Key<T>

inline fun <reified T> CoroutineScope.saveData(key: String, value: T) {
    logD("保存键对", "key=$key value=$value")
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
    val mediaUpdateCheck by lazyDataStoreStateFlow(Const.Setting.MEDIA_UPDATE_CHECK, true)
    val mediaUpdateCheckInterval by lazyDataStoreStateFlow(
        Const.Setting.MEDIA_UPDATE_CHECK_INTERVAL, "2_HOURS"
    )
    val mediaUpdateCheckLastTime by lazyDataStoreStateFlow(
        Const.Setting.MEDIA_UPDATE_CHECK_LAST_TIME, -1L
    )
    val mediaUpdateOnMeteredNet by lazyDataStoreStateFlow(
        Const.Setting.MEDIA_UPDATE_CHECK_ON_METERED_NET, false
    )
    val commonlyUsedVideoSpeed by lazyDataStoreStateFlow(
        Const.Setting.COMMONLY_USED_VIDEO_SPEED, 2.0f
    )
    val danmakuSendColor by lazyDataStoreStateFlow(Const.Setting.DANMAKU_SEND_COLOR, Color.WHITE)
    val autoSeekVidePosition by lazyDataStoreStateFlow(Const.Setting.AUTO_SEEK_PLAY_POSITION, false)

    val combineSearchIgnorePlugins by lazyDataStoreStateFlow(
        Const.Setting.COMBINE_SEARCH_IGNORE_PLUGINS,
        ""
    )

    val danmakuTextScalePercent by lazyDataStoreStateFlow(
        Const.Setting.DANMAKU_TEXT_SCALE_PERCENT,
        0.8f
    )

    val danmakuTopDisplayAreaMode by lazyDataStoreStateFlow(
        Const.Setting.DANMAKU_TOP_DISPLAY_AREA_MODE,
        50
    )

    val debugVersionUpdateId by lazyDataStoreStateFlow(
        Const.Setting.DEBUG_VERSION, ""
    )

    val announcementVersion by lazyDataStoreStateFlow(Const.Setting.ANNOUNCEMENT, 0)
}