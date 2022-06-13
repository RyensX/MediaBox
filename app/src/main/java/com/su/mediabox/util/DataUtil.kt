package com.su.mediabox.util

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class DataState<out D> {

    object Init : DataState<Nothing>()

    object Loading : DataState<Nothing>()

    class Success<D>(var data: D? = null) : DataState<D>() {

        /**
         * 也可以绑定某个生命周期单例使用
         */
        companion object {

            private val insMap = mutableMapOf<Any, Success<*>>()

            fun Any.destroySuccessIns() = insMap.remove(this)

            fun <D> Any.successIns(): Success<D> {
                return (insMap[this] ?: Success<D>().also {
                    insMap[this] = it
                }) as Success<D>
            }
        }
    }

    class Failed(val throwable: Throwable?) : DataState<Nothing>()
}

/**
 * 自动装入一个[MutableDynamicReferenceListData]并返回
 */
fun <T> DataState.Success<MutableDynamicReferenceListData<T>>.data() =
    data ?: MutableDynamicReferenceListData<T>().also { data = it }

/**
 * 动态引用列表数据/
 *
 * 每次修改的数据得到的data引用都不同，因此可触发[RecyclerView]异步更新
 */
abstract class DynamicReferenceListData<T> {
    /**
     * 必须要在数据动作完成后才能调用，否则得到的可能不是最新数据
     */
    abstract val data: List<T>
    var lastLoad: Int = 0
        protected set
}

class MutableDynamicReferenceListData<T> : DynamicReferenceListData<T>() {

    override val data: List<T> get() = dataRef

    private var dataRef = mutableListOf<T>()
    private var tmpDataRef = mutableListOf<T>()

    suspend fun putData(data: List<T>): List<T> =
        if (data.isEmpty()) dataRef
        else withContext(Dispatchers.Default) {
            tmpDataRef.clear()
            tmpDataRef.addAll(data)
            lastLoad = data.size
            val tmp = dataRef
            dataRef = tmpDataRef
            tmpDataRef = tmp
            dataRef
        }

    /**
     * @param startIndex -1则表示添加在末尾
     */
    suspend fun appendData(data: List<T>, startIndex: Int = -1): List<T> =
        if (data.isEmpty()) dataRef
        else withContext(Dispatchers.Default) {
            tmpDataRef.clear()
            tmpDataRef.addAll(dataRef)
            if (startIndex == -1)
                tmpDataRef.addAll(data)
            else
                tmpDataRef.addAll(startIndex, data)
            lastLoad = data.size
            val tmp = dataRef
            dataRef = tmpDataRef
            tmpDataRef = tmp
            dataRef
        }

    suspend fun removeData(startIndex: Int, size: Int): List<T> =
        if (data.isEmpty() || size == 0) dataRef
        else withContext(Dispatchers.Default) {
            tmpDataRef.clear()
            tmpDataRef.addAll(dataRef)
            val a = tmpDataRef.iterator()
            val endIndex = startIndex + size
            var index = 0
            while (a.hasNext()) {
                a.next()
                if (index >= startIndex) {
                    if (index < endIndex)
                        a.remove()
                    else
                        break
                }
                index++
            }
            val tmp = dataRef
            dataRef = tmpDataRef
            tmpDataRef = tmp
            dataRef
        }

    fun replaceData(index: Int, data: T) {
        if (index in dataRef.indices)
            dataRef[index] = data
    }

    suspend fun removeData(data: T): List<T> =
        withContext(Dispatchers.Default) {
            removeData(dataRef.indexOf(data), 1)
        }

}