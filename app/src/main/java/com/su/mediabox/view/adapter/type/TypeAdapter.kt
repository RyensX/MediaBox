package com.su.mediabox.view.adapter.type

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.pluginapi.data.*
import com.su.mediabox.util.*
import com.su.mediabox.util.Util.withoutExceptionGet
import com.su.mediabox.view.viewcomponents.*

typealias DataViewMapList = ArrayList<Pair<Class<Any>, Class<TypeViewHolder<Any>>>>

class TypeAdapter(
    private val bindingContext: Context,
    dataViewMapList: DataViewMapList,
    diff: DiffUtil.ItemCallback<Any>,
    private val bindingRecyclerView: RecyclerView? = null,
    var dataViewMapCache: Boolean = true
) :
    ListAdapter<Any, TypeViewHolder<Any>>(diff) {

    private var currentData: List<Any>? = null

    override fun onViewAttachedToWindow(holder: TypeViewHolder<Any>) {
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: TypeViewHolder<Any>) {
        holder.onViewAttachedToWindow()
    }

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
            logD("获取缓存池", "key=$key")
            return recycledViewPools[key] ?: RecyclerView.RecycledViewPool()
                .also { recycledViewPools[key] = it }
        }

        fun clearRecycledViewPool(dataViewMapList: DataViewMapList) =
            recycledViewPools.remove(dataViewMapList.getDataViewMapListKey())

        init {
            //初始化全局数据视图对照表
            globalDataViewMap
                .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
                .registerDataViewMap<TagFlowData, TagFlowViewHolder>()
                .registerDataViewMap<EpisodeListData, VideoPlayListViewHolder>()
                .registerDataViewMap<Cover1Data, Cover1ViewHolder>()
                .registerDataViewMap<EpisodeData, VideoPlayListViewHolder.EpisodeViewHolder>()
                .registerDataViewMap<MediaInfo1Data, MediaInfo1ViewHolder>()
                .registerDataViewMap<TagData, TagViewHolder>()
                .registerDataViewMap<LongTextData, LongTextViewHolder>()
                .registerDataViewMap<MediaInfo2Data, MediaInfo2ViewHolder>()
                .registerDataViewMap<ViewPagerData, ViewPagerViewHolder>()
                .registerDataViewMap<BannerData, BannerViewHolder>()
                .registerDataViewMap<HorizontalListData, HorizontalListViewHolder>()
            for (viewType in globalDataViewMap.indices)
            //全局Pool提供15个槽位
                globalTypeRecycledViewPool.setMaxRecycledViews(viewType, 15)
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

    val vhCreateDsLs by unsafeLazy { mutableMapOf<Class<*>, TypeViewHolder<*>.() -> Unit>() }

    /**
     * 添加某种VH创建时调用的DSL，可用于添加点击、长按、触摸等，重复添加会覆盖。
     *
     * 注意复用问题，不要在Listener之类内部直接调用外部的对象，并且尽量使用[TypeViewHolder.bindingContext]和[TypeViewHolder.bindingTypeAdapter]
     *
     * @param V VH类型，[TypeViewHolder]则表示全部VH
     *
     * 举例:
     * ```
     * vHCreateDSL<TypeViewHolder<Any>> {
     *      itemView.setOnClickListener {
     *          "任意VH点击".showToast()
     *      }
     * }
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified V : TypeViewHolder<*>> vHCreateDSL(noinline dsl: V.() -> Unit) {
        vhCreateDsLs[V::class.java] = dsl as TypeViewHolder<*>.() -> Unit
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getData(position: Int) = withoutExceptionGet { getItem(position) as T }

    public override fun getItem(position: Int) = withoutExceptionGet { super.getItem(position) }

    fun checkDataIsSame(list: List<Any>?) = list === currentData

    override fun submitList(list: List<Any>?) {
        submitList(list, null)
    }

    override fun submitList(list: List<Any>?, commitCallback: Runnable?) {
        //更新映射
        if (!checkDataIsSame(list)) {
            currentData = list
            if (dataViewMapCache)
                clearDataViewPosMap()
        }
        //更新LayoutConfig
        list?.getOrNull(0)?.let { data ->
            if (data is BaseData)
                data.layoutConfig?.apply {
                    logD("检测到配置", this.toString())
                    //spanCount
                    val layoutManager = bindingRecyclerView?.layoutManager
                    if (layoutManager is GridLayoutManager) {
                        logD("设置", "spanCount=$spanCount")
                        layoutManager.spanCount = spanCount
                    }
                    //边距
                    bindingRecyclerView?.getFirstItemDecorationBy<DynamicGridItemDecoration>()
                        ?.let {
                            logD("设置", "spacing=$itemSpacing")
                            it.itemSpacing = itemSpacing
                        }
                }
        }
        super.submitList(list, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder<Any> =
        if (viewType == UNKNOWN_TYPE)
            TypeViewHolder.UnknownTypeViewHolder(parent)
        else {
            try {
                val vhClass = dataViewMapList[viewType].second
                logD("创建VH", vhClass.simpleName)
                vhClass.getDeclaredConstructor(ViewGroup::class.java)
                    .apply { isAccessible = true }
                    .newInstance(parent)
                    .apply {
                        //TODO 在复用时可能会出现问题
                        (vhCreateDsLs[vhClass]
                        //没有具体的则使用全局的
                            ?: vhCreateDsLs[TypeViewHolder::class.java])?.invoke(
                            this
                        )
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                logD("VH创建错误", e.message ?: viewType.toString())
                TypeViewHolder.UnknownTypeViewHolder(parent)
            }
        }

    override fun onBindViewHolder(holder: TypeViewHolder<Any>, position: Int) {
        holder.checkBindingContext(bindingContext)
        getItem(position)?.also {
            holder.onBind(it)
        } ?: logD("无法绑定", "$holder position:$position")
    }

    override fun onBindViewHolder(
        holder: TypeViewHolder<Any>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.checkBindingContext(bindingContext)
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

        /**
         * 对于大多数typeData来说并没有主键确定他们为同一个目标且不会被更改，内容相同就行
         */
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
            oldItem == newItem

        /**
         * 对比内容，建议使用data class，会自动实现内容equals
         */
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
            oldItem == newItem

    }
}