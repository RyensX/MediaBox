package com.su.mediabox.util

sealed class DataState<out D> {

    object Init : DataState<Nothing>()

    object Loading : DataState<Nothing>()

    class SingleSuccess<D>(val data: D? = null) : DataState<D>()

    /**
     * 内部data为可append的集合
     */
    class AppendableListDataSuccess<D> private constructor() : DataState<D>() {

        companion object {

            private val insMap = mutableMapOf<Any, AppendableListDataSuccess<*>>()

            fun destroyIns(obj: Any) = insMap.remove(obj)

            fun <D> getIns(obj: Any): AppendableListDataSuccess<D> {
                return (insMap[obj] ?: AppendableListDataSuccess<D>().also {
                    insMap[obj] = it
                }) as AppendableListDataSuccess<D>
            }
        }

        var data: List<D>? = null
            private set

        var isLoadEmptyData = false
            private set

        fun putData(data: List<D>?): AppendableListDataSuccess<D> {
            this.data = data
            return this
        }

        fun appendData(appendData: List<D>?): AppendableListDataSuccess<D> {
            isLoadEmptyData = appendData.isNullOrEmpty()
            if (appendData.isNullOrEmpty())
                return this
            val list = mutableListOf<D>()
            data?.also { list.addAll(it) }
            list.addAll(appendData)
            data = list
            return this
        }
    }

    class Failed(val throwable: Throwable?) : DataState<Nothing>()
}