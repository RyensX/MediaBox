package com.su.mediabox.util

import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.removeAllObserver() {
    LiveData::class.java.geMemberOrNull<SafeIterableMap<Observer<T>, *>>("mObservers", this)
        ?.forEach {
            removeObserver(it.key)
        }
}

fun <T> MutableLiveData<T>.toLiveData(): LiveData<T> = this

abstract class WrapperLiveData<T>(val originalLiveData: MutableLiveData<*>) : LiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) =
        originalLiveData.observe(owner, observer as Observer<in Any>)

    override fun observeForever(observer: Observer<in T>) =
        originalLiveData.observeForever(observer as Observer<in Any>)

    override fun removeObserver(observer: Observer<in T>) =
        originalLiveData.removeObserver(observer as Observer<in Any>)

    override fun removeObservers(owner: LifecycleOwner) = originalLiveData.removeObservers(owner)
    override fun postValue(value: T) = originalLiveData.postValue(value as Nothing?)
    override fun setValue(value: T) {
        originalLiveData.value = value
    }

    override fun getValue(): T? = originalLiveData.value as? T
    override fun hasObservers(): Boolean = originalLiveData.hasObservers()
    override fun hasActiveObservers(): Boolean = originalLiveData.hasActiveObservers()

}

/**
 * 专用于list的LiveData，可直接添加并推送到ListAdapter更新
 */
class MutableListLiveData<T>(data: MutableList<T> = mutableListOf()) :
    MutableLiveData<MutableList<T>>(data) {

    private var tmpList = mutableListOf<T>()

    fun addData(data: T) {
        value?.run {
            add(data)
            switchDataRefUpdate()
        }
    }

    fun removeData(data: T) {
        value?.run {
            remove(data)
            switchDataRefUpdate()
        }
    }

    private fun addDataList(data: List<T>) {
        value?.run {
            addAll(data)
            switchDataRefUpdate()
        }
    }

    private fun switchDataRefUpdate() {
        value?.let {
            tmpList.clear()
            tmpList.addAll(it)
            val tmpRef = tmpList
            tmpList = it
            postValue(tmpRef)
        }
    }
}