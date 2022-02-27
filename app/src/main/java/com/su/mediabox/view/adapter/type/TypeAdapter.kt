package com.su.mediabox.view.adapter.type

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

typealias DataViewMap = ArrayList<Pair<Class<Any>, Class<TypeViewHolder<Any>>>>

class TypeAdapter(
    private val dataViewMap: DataViewMap,
    diff: DiffUtil.ItemCallback<Any>
) :
    ListAdapter<Any, TypeViewHolder<Any>>(diff) {

    companion object {
        const val UNKNOWN_TYPE = -1
        val globalDataViewMap = DataViewMap()

        val globalTypeRecycledViewPool by lazy(LazyThreadSafetyMode.NONE) { RecyclerView.RecycledViewPool() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(position: Int) = getItem(position) as? T

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder<Any> =
        if (viewType == UNKNOWN_TYPE)
            TypeViewHolder.UnknownTypeViewHolder(parent)
        else {
            try {
                dataViewMap[viewType].second
                    .getConstructor(ViewGroup::class.java)
                    .newInstance(parent)
            } catch (e: Exception) {
                TypeViewHolder.UnknownTypeViewHolder(parent)
            }
        }

    override fun onBindViewHolder(holder: TypeViewHolder<Any>, position: Int) {
        getItem(position)?.also {
            holder.onBind(it)
        }
    }

    override fun onBindViewHolder(
        holder: TypeViewHolder<Any>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        getItem(position)?.also {
            holder.onBind(it, payloads)
        }
    }

    /**
     * 根据类型在数据视图映射表里查找
     * @return 返回结果是查找目前的index
     */
    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let { data ->
            dataViewMap.forEachIndexed { index, pair ->
                if (data.javaClass == pair.first)
                    return index
            }
            UNKNOWN_TYPE
        } ?: UNKNOWN_TYPE
    }

    object DefaultDiff : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
            oldItem === newItem

        /**
         * 对比内容，建议使用data class，会自动实现内容equals
         */
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
            oldItem == newItem

    }
}