package com.su.mediabox.view.adapter.type

import androidx.recyclerview.widget.*
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.getFirstItemDecorationBy

fun RecyclerView.typeAdapter() =
    adapter as? TypeAdapter ?: throw RuntimeException("当前绑定的适配器不是TypeAdapter")

/**
 * 线性列表
 * @param orientation 列表方向
 * @param reverseLayout 是否反转列表
 */
fun RecyclerView.linear(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    stackFromEnd: Boolean = false,
    initialPrefetchItemCount: Int = 4
): RecyclerView {
    layoutManager = LinearLayoutManager(context, orientation, reverseLayout).apply {
        this.stackFromEnd = stackFromEnd
        this.initialPrefetchItemCount = initialPrefetchItemCount
    }
    return this
}

/**
 * 网格列表
 * @param spanCount 网格跨度数量
 * @param orientation 列表方向
 * @param reverseLayout 是否反转
 */
fun RecyclerView.grid(
    spanCount: Int = 2,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    initialPrefetchItemCount: Int = 4
): RecyclerView {
    layoutManager = GridLayoutManager(context, spanCount, orientation, reverseLayout).apply {
        this.initialPrefetchItemCount = initialPrefetchItemCount
    }
    return this
}

/**
 * 动态网格列表，需要配合[BaseData]使用
 * @param orientation 列表方向
 * @param reverseLayout 是否反转
 */
fun RecyclerView.dynamicGrid(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    spacing: Int = 8.dp,
    reverseLayout: Boolean = false
): RecyclerView {
    layoutManager =
        GridLayoutManager(context, Constant.DEFAULT_SPAN_COUNT, orientation, reverseLayout).apply {
            if (spacing != 0)
                addItemDecoration(DynamicGridItemDecoration(spacing))
            //动态spanSize
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    typeAdapter().getData<BaseData>(position)?.spanSize
                        ?: Constant.DEFAULT_SPAN_SIZE
            }
        }
    return this
}

/**
 * 瀑布列表
 * @param spanCount 网格跨度数量
 * @param orientation 列表方向
 * @param reverseLayout 是否反转
 */
fun RecyclerView.staggered(
    spanCount: Int = 2,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
): RecyclerView {
    layoutManager = StaggeredGridLayoutManager(spanCount, orientation).apply {
        this.reverseLayout = reverseLayout
    }
    return this
}

/**
 * @param useSharedRecycledViewPool 使用相同映射表共享的VH缓存池
 */
@Suppress("UNCHECKED_CAST")
inline fun RecyclerView.initTypeList(
    dataViewMap: DataViewMapList = TypeAdapter.globalDataViewMap,
    diff: DiffUtil.ItemCallback<*> = TypeAdapter.DefaultDiff,
    useSharedRecycledViewPool: Boolean = true,
    block: TypeAdapter.(RecyclerView) -> Unit,
): TypeAdapter {
    //默认关闭
    isNestedScrollingEnabled = false
    if (useSharedRecycledViewPool) {
        setRecycledViewPool(
            //如果是全局映射表则不再额外计算
            if (dataViewMap == TypeAdapter.globalDataViewMap)
                TypeAdapter.globalTypeRecycledViewPool
            else
                TypeAdapter.getRecycledViewPool(dataViewMap)
        )
    }
    return TypeAdapter(
        context,
        dataViewMap,
        diff as DiffUtil.ItemCallback<Any>,
        getFirstItemDecorationBy<DynamicGridItemDecoration>()?.let { this }
    ).apply {
        block(this@initTypeList)
        adapter = this
    }
}

fun RecyclerView.submitList(list: List<Any>?) = typeAdapter().submitList(list)

/**
 * 注册一个数据与视图关系到全局映射
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified D : Any, reified V : TypeViewHolder<D>> DataViewMapList.registerDataViewMap(): DataViewMapList {
    add(
        Pair(
            D::class.java as Class<Any>,
            V::class.java as Class<TypeViewHolder<Any>>
        )
    )
    return this
}

/**
 * 查询特定类型第一个符合条件的数据
 * @param startIndex 起始index
 * @param step 步长，可用于控制速度和方向，如-1则向前查找
 */
inline fun <reified T> TypeAdapter.findTypeData(startIndex: Int = 0, step: Int = 1): T? {
    var start = startIndex
    while (start in 0 until itemCount) {
        getItem(start).let {
            if (it is T)
                return it
        }
        start += step
    }
    return null
}