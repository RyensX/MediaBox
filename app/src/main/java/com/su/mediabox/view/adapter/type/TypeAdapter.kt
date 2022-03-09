package com.su.mediabox.view.adapter.type

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.view.viewcomponents.*
import com.su.skin.SkinManager

typealias DataViewMapList = ArrayList<Pair<Class<Any>, Class<TypeViewHolder<Any>>>>

class TypeAdapter(
    dataViewMapList: DataViewMapList,
    diff: DiffUtil.ItemCallback<Any>
) :
    ListAdapter<Any, TypeViewHolder<Any>>(diff) {

    companion object {
        const val UNKNOWN_TYPE = -1
        val globalDataViewMap = DataViewMapList()

        val globalTypeRecycledViewPool by lazy(LazyThreadSafetyMode.NONE) { RecyclerView.RecycledViewPool() }

        /**
         * VH缓存池集，每种映射表对应一个VH缓存池
         */
        private val recycledViewPools = mutableMapOf<Int, RecyclerView.RecycledViewPool>()
        fun getRecycledViewPool(dataViewMapList: DataViewMapList): RecyclerView.RecycledViewPool {
            var key = 0
            dataViewMapList.forEach {
                key += it.first.hashCode() + it.second.hashCode()
            }
            return recycledViewPools[key] ?: RecyclerView.RecycledViewPool()
                .also { recycledViewPools[key] = it }
        }

        init {
            //初始化全局数据视图对照表
            Thread {
                globalDataViewMap
                    .registerDataViewMap<TextData, TextViewHolder>()
                    .registerDataViewMap<TagFlowData, TagFlowViewHolder>()
                    .registerDataViewMap<VideoPlayListData, VideoPlayListViewHolder>()
                    .registerDataViewMap<VideoCover1Data, VideoCover1ViewHolder>()
                    .registerDataViewMap<EpisodeData, VideoPlayListViewHolder.EpisodeViewHolder>()
                    .registerDataViewMap<VideoGridItemData, VideoGridItemViewHolder>()
                    .registerDataViewMap<VideoGridData, VideoGridViewHolder>()
                    .registerDataViewMap<TagData, TagViewHolder>()
                    .registerDataViewMap<LongTextData, LongTextViewHolder>()
            }.start()
        }
    }

    private val dataViewPosMap = mutableMapOf<Int, Int>()

    fun clearDataViewPosMap() = dataViewPosMap.clear()

    /**
     * 注意增删元素需要手动调用[clearDataViewPosMap]
     */
    var dataViewMapList: DataViewMapList = dataViewMapList
        set(value) {
            field = value
            clearDataViewPosMap()
        }

    /**
     * 用于父子VH交换信息
     */
    private var tag: Any? = null

    fun setTag(data: Any?) {
        tag = data
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTag(): T? = try {
        tag as? T
    } catch (e: Exception) {
        null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(position: Int) = getItem(position) as? T

    override fun submitList(list: List<Any>?, commitCallback: Runnable?) {
        if (list != currentList) {
            dataViewPosMap.clear()
        }
        super.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder<Any> =
        if (viewType == UNKNOWN_TYPE)
            TypeViewHolder.UnknownTypeViewHolder(parent)
        else {
            try {
                dataViewMapList[viewType].second
                    .getConstructor(ViewGroup::class.java)
                    .newInstance(parent)
            } catch (e: Exception) {
                TypeViewHolder.UnknownTypeViewHolder(parent)
            }
        }

    override fun onBindViewHolder(holder: TypeViewHolder<Any>, position: Int) {
        SkinManager.applyViews(holder.itemView)
        getItem(position)?.also {
            holder.onBind(it)
        }
    }

    override fun onBindViewHolder(
        holder: TypeViewHolder<Any>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        SkinManager.applyViews(holder.itemView)
        getItem(position)?.also {
            holder.onBind(it, payloads)
        }
    }

    /**
     * 根据类型在数据视图映射表里查找
     * @return 返回结果是查找目前的index
     */
    override fun getItemViewType(position: Int): Int {
        return dataViewPosMap[position] ?: getItem(position)?.let { data ->
            dataViewMapList.forEachIndexed { index, pair ->
                //必须对应真实类型，即使是子类也是不同的
                if (data.javaClass == pair.first) {
                    dataViewPosMap[position] = index
                    return index
                }
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