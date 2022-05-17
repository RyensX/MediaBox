package com.su.mediabox.util


sealed class DataState {

    object INIT : DataState()

    object LOADING : DataState()

    class SUCCESS<D> private constructor() : DataState() {

        companion object {

            private val insMap = mutableMapOf<Any, SUCCESS<*>>()

            fun destroyIns(obj: Any) {
                insMap.remove(obj)
            }

            fun <D> getIns(obj: Any): SUCCESS<D> {
                return (insMap[obj] ?: SUCCESS<D>().also { insMap[obj] = it }) as SUCCESS<D>
            }
        }

        private var _data: List<D>? = null
        fun <D> getData(): List<D>? = Util.withoutExceptionGet { _data as List<D> }

        var isLoadEmptyData = false
            private set

        fun appendData(appendData: List<D>?): SUCCESS<D> {
            isLoadEmptyData = appendData.isNullOrEmpty()
            if (appendData.isNullOrEmpty())
                return this
            val list = mutableListOf<D>()
            _data?.also { list.addAll(it) }
            list.addAll(appendData)
            _data = list
            return this
        }
    }

    class FAILED(val throwable: Throwable?) : DataState()
}