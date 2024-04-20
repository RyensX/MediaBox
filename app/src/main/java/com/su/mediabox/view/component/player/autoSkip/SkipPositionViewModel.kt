package com.su.mediabox.view.component.player.autoSkip

import androidx.lifecycle.*
import com.su.mediabox.database.entity.SkipPosEntity
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.util.removeAllObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkipPositionViewModel : ViewModel() {

    private val dao = getOfflineDatabase().skipPosRecordDao()

    var videoName: String = ""
        private set

    private var skipPosListLiveData: LiveData<List<SkipPosEntity>?>? = null

    private val startTimeInner = MutableLiveData(-1)
    private val endTimeInner = MutableLiveData(-1)
    val startTime: LiveData<Int> = startTimeInner
    val endTime: LiveData<Int> = endTimeInner

    val canSaveSkip: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(startTime) { time ->
            value = time > -1 && endTime.value.let { it != null && it > -1 }
        }
        addSource(endTime) { time ->
            value = time > -1 && startTime.value.let { it != null && it > -1 }
        }
    }

    fun resetTimeData() {
        startTimeInner.postValue(-1)
        endTimeInner.postValue(-1)
    }

    fun setStartTime(time: Int) {
        startTimeInner.postValue(time)
    }

    fun setEndTime(time: Int) {
        endTimeInner.postValue(time)
    }

    fun getSkipPosList(video: String): LiveData<List<SkipPosEntity>?> {
        videoName = video
        val ld = dao.queryList(video)
        skipPosListLiveData = ld
        return ld
    }

    fun putPlayPosition(
        video: String = videoName,
        startPosition: Long = startTime.value?.toLong() ?: -1,
        endDuration: Long = endTime.value?.toLong() ?: -1,
        desc: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            //dao.insert(SkipPosEntity(video, startPosition, endDuration - startPosition, desc, true))
            dao.insertData(video, startPosition, endDuration - startPosition, desc, true)
            resetTimeData()
        }
    }

    fun enable(id: Int, enable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.enable(id, enable)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(id)
        }
    }

    fun checkSkip(sec: Int, onEqual: (SkipPosEntity) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            skipPosListLiveData?.value?.find { convertTime(it.position.toInt()) == sec && it.enable }
                ?.let {
                    viewModelScope.launch(Dispatchers.Main) {
                        onEqual(it)
                    }
                }
        }
    }

    fun convertTime(ms: Int) = ms / 1000
}