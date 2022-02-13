package com.su.mediabox.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.plugin.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PluginViewModel : ViewModel() {

    private val _pluginLiveData = MutableLiveData<List<PluginInfo>>()
    private val pluginIntent = Intent(Constant.PLUGIN_ACTION)

    val pluginLiveData: LiveData<List<PluginInfo>>
        get() = _pluginLiveData

    fun scanPlugin(packageManager: PackageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val plugin = packageManager.queryIntentActivities(pluginIntent, 0).map {
                PluginInfo(
                    it.activityInfo.packageName,
                    it.activityInfo.name,
                    it.loadLabel(packageManager).toString(),
                    it.loadIcon(packageManager),
                    it.activityInfo.applicationInfo.sourceDir
                )
            }
            _pluginLiveData.postValue(plugin)
        }
    }
}