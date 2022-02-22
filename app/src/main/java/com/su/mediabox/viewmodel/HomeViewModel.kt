package com.su.mediabox.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.R
import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.components.IHomeComponent
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.SerializableRecycledViewPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val homeModel: IHomeComponent by lazy(LazyThreadSafetyMode.NONE) {
        PluginManager.acquireComponent(IHomeComponent::class.java)
    }
    val childViewPool = SerializableRecycledViewPool()
    val viewPool = SerializableRecycledViewPool()
    var allTabList: MutableList<TabBean> = ArrayList()
    var mldGetAllTabList: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllTabData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                homeModel.getAllTabData().apply {
                    allTabList.clear()
                    allTabList.addAll(this)
                    mldGetAllTabList.postValue(true)
                }

            } catch (e: Exception) {
                allTabList.clear()
                mldGetAllTabList.postValue(false)
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast(
                    Toast.LENGTH_LONG
                )
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}