package com.su.mediabox.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.su.mediabox.R
import com.su.mediabox.util.ResourceUtil

class PluginUpdateViewModel : ViewModel() {
    private val updatableCount = MutableLiveData(0)
    private val downloadableCount = MutableLiveData(0)

    private val updatableCountColor = Color.parseColor("#3DDC84")
    private val downloadableColor = ResourceUtil.getColor(R.color.main_color_2_skin)

    /**
     * Pair<可用数目，标签颜色>
     *
     * 可用数目：值可为下载和可升级数，可升级数优先显示
     */
    val repoAvailableData: LiveData<Pair<Int, Int>> = MediatorLiveData<Pair<Int, Int>>().apply {
        addSource(downloadableCount) { count ->
            if (updatableCount.value.let { it == null || it == 0 })
                value = Pair(count, downloadableColor)
        }
        addSource(updatableCount) {
            if (it > 0)
                value = Pair(it, updatableCountColor)
        }
    }

    fun updateCountData(updatable: Int, downloadable: Int) {
        updatableCount.postValue(updatable)
        downloadableCount.postValue(downloadable)
    }
}