package com.su.mediabox.view.adapter.type

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.util.Util.withoutExceptionGet
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.view.viewcomponents.*
import com.su.skin.SkinManager

typealias DataViewMapList = ArrayList<Pair<Class<Any>, Class<TypeViewHolder<Any>>>>

class TypeAdapter(
    dataViewMapList: DataViewMapList,
    diff: DiffUtil.ItemCallback<Any>,
    var dataViewMapCache: Boolean = true
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
        private fun DataViewMapList.getDataViewMapListKey() = run {
            var key = 0
            forEach {
                key += it.first.hashCode() + it.second.hashCode()
            }
            key
        }

        fun getRecycledViewPool(dataViewMapList: DataViewMapList): RecyclerView.RecycledViewPool {
            val key = dataViewMapList.getDataViewMapListKey()
            return recycledViewPools[key] ?: RecyclerView.RecycledViewPool()
                .also { recycledViewPools[key] = it }
        }

        fun clearRecycledViewPool(dataViewMapList: DataViewMapList) =
            recycledViewPools.remove(dataViewMapList.getDataViewMapListKey())

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
                    .registerDataViewMap<GridData, GridViewHolder>()
                    .registerDataViewMap<TagData, TagViewHolder>()
                    .registerDataViewMap<LongTextData, LongTextViewHolder>()
                    .registerDataViewMap<VideoInfoItemData, VideoInfoItemViewHolder>()
                    .registerDataViewMap<ViewPagerData, ViewPagerViewHolder>()
            }.start()
        }
    }

    /**
     * 映射缓存，<数据pos，对应VH在映射表的pos>
     */
    private val dataViewPosMap = mutableMapOf<Int, Int>()

    fun clearDataViewPosMap() = dataViewPosMap.clear()

    /**
     * <数据CLass,VH的Class>
     *
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
    val tags by lazy { mutableMapOf<String, Any?>() }

    /**
     * 不主动指定[key]要非常注意null，key会变成""覆盖或者设置错值
     */
    fun setTag(data: Any?, key: String = data?.javaClass?.simpleName ?: "") {
        tags[key] = data
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getTag(key: String = T::class.java.simpleName): T? =
        withoutExceptionGet { tags[key] as? T }

    //<VH的Class,对应Listener>
    val clickListeners = mutableMapOf<Class<*>, TypeViewHolder<*>.(position: Int) -> Unit>()
    val longClickListeners = mutableMapOf<Class<*>, TypeViewHolder<*>.(position: Int) -> Boolean>()

    /**
     * 为某种VH的itemView添加点击监听，重复添加会覆盖
     * @param V VH类型
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified V : TypeViewHolder<*>> addViewHolderClickListener(noinline onClick: V.(position: Int) -> Unit) {
        clickListeners[V::class.java] = onClick as TypeViewHolder<*>.(Int) -> Unit
    }

    /**
     * 为某种VH的itemView添加长按监听，重复添加会覆盖
     * @param V VH类型
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified V : TypeViewHolder<*>> addViewHolderLongClickListener(noinline onClick: V.(position: Int) -> Boolean) {
        longClickListeners[V::class.java] = onClick as TypeViewHolder<*>.(Int) -> Boolean
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(position: Int) = withoutExceptionGet { getItem(position) as? T }

    override fun submitList(list: List<Any>?) {
        if (dataViewMapCache && (list == null || list != currentList)) {
            clearDataViewPosMap()
        }
        super.submitList(list)
    }

    override fun submitList(list: List<Any>?, commitCallback: Runnable?) {
        if (dataViewMapCache && (list == null || list != currentList)) {
            clearDataViewPosMap()
        }
        super.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder<Any> =
        if (viewType == UNKNOWN_TYPE)
            TypeViewHolder.UnknownTypeViewHolder(parent)
        else {
            try {
                val vhClass = dataViewMapList[viewType].second
                vhClass.getDeclaredConstructor(ViewGroup::class.java)
                    .apply { isAccessible = true }
                    .newInstance(parent)
                    .apply {
                        //点击
                        if (clickListeners[vhClass] != null)
                            setOnClickListener(itemView) { pos ->
                                (this as TypeViewHolder<*>).bindingTypeAdapter.clickListeners[vhClass]?.also {
                                    it(pos)
                                }
                            }
                        //长按
                        if (longClickListeners[vhClass] != null)
                            setOnLongClickListener(itemView) { pos ->
                                (this as TypeViewHolder<*>).bindingTypeAdapter.longClickListeners[vhClass]?.let {
                                    it(pos)
                                } ?: true
                            }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("VH创建错误", e.message ?: viewType.toString())
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
     * @return 返回结果是查找目前类型在映射表里的index
     */
    override fun getItemViewType(position: Int): Int {
        return (if (dataViewMapCache) dataViewPosMap[position] else null)
            ?: getItem(position)?.let { data ->
                dataViewMapList.forEachIndexed { index, pair ->
                    //必须对应真实类型，即使是子类也是不同的
                    if (data.javaClass == pair.first) {
                        if (dataViewMapCache)
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