package com.su.mediabox.util

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class DataState<out D> {

    object Init : DataState<Nothing>()

    object Loading : DataState<Nothing>()

    class Success<D>(var data: D? = null) : DataState<D>() {

        /**
         * 可单例使用也可哟
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
 * 动态引用列表数据
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

    private var action: DataAction? = null

    @Suppress("UNCHECKED_CAST")
    private suspend fun apply(): List<T> = action?.let {
        withContext(Dispatchers.Default) {
            tmpDataRef.clear()
            when (it) {
                is DataAction.PUT -> {
                    tmpDataRef.addAll(it.newData as List<T>)
                    lastLoad = it.newData.size
                }
                is DataAction.APPEND -> {
                    tmpDataRef.addAll(dataRef)
                    if (it.startIndex == -1)
                        tmpDataRef.addAll(it.appendData as List<T>)
                    else
                        tmpDataRef.addAll(it.startIndex, it.appendData as List<T>)
                    lastLoad = it.appendData.size
                }
                is DataAction.REMOVE -> {
                    tmpDataRef.addAll(dataRef)
                    val a = tmpDataRef.iterator()
                    val endIndex = it.startIndex + it.size
                    var index = 0
                    while (a.hasNext()) {
                        if (index >= it.startIndex) {
                            if (index < endIndex)
                                a.remove()
                            else
                                break
                        }
                        index++
                    }
                }
            }
            //注意，在被Rv异步更新时原dataRef是不能修改的，否则会触发 ava.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter
            val tmp = dataRef
            dataRef = tmpDataRef
            tmpDataRef = tmp
            dataRef
        }
    } ?: dataRef

    suspend fun putData(data: List<T>): List<T> {
        action = DataAction.PUT(data)
        return apply()
    }

    /**
     * @param startIndex -1则表示添加在末尾
     */
    suspend fun appendData(data: List<T>, startIndex: Int = -1): List<T> {
        action = DataAction.APPEND(data, startIndex)
        return apply()
    }

    suspend fun removeData(startIndex: Int, size: Int): List<T> {
        action = DataAction.REMOVE(startIndex, size)
        return apply()
    }

    suspend fun removeData(data: T): List<T> {
        action = DataAction.REMOVE(dataRef.indexOf(data), 1)
        return apply()
    }

    private sealed class DataAction {
        class PUT(val newData: Any) : DataAction()
        class APPEND(val appendData: Any, val startIndex: Int) : DataAction()
        class REMOVE(val startIndex: Int, val size: Int) : DataAction()
    }
}