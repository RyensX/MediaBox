package com.su.mediabox.view.adapter.type

import androidx.recyclerview.widget.*


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
    stackFromEnd: Boolean = false
): RecyclerView {
    layoutManager = LinearLayoutManager(context, orientation, reverseLayout).apply {
        this.stackFromEnd = stackFromEnd
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
    reverseLayout: Boolean = false
): RecyclerView {
    layoutManager = GridLayoutManager(context, spanCount, orientation, reverseLayout)
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

inline fun RecyclerView.initTypeList(
    dataViewMap: DataViewMap = TypeAdapter.globalDataViewMap,
    diff: DiffUtil.ItemCallback<Any> = TypeAdapter.DefaultDiff,
    useSharedRecycledViewPool: Boolean = true,
    block: TypeAdapter.(RecyclerView) -> Unit,
): TypeAdapter {
    if (useSharedRecycledViewPool) {
        setRecycledViewPool(TypeAdapter.globalTypeRecycledViewPool)
        if (layoutManager is LinearLayoutManager)
            (layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
    }
    return TypeAdapter(dataViewMap, diff).apply {
        block(this@initTypeList)
        adapter = this
    }
}

fun RecyclerView.submitList(list: List<Any>) = typeAdapter().submitList(list)

/**
 * 注册一个数据与视图关系到全局映射
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified D : Any, reified V : TypeViewHolder<*>> DataViewMap.registerDataViewMap(): DataViewMap {
    add(
        Pair(
            D::class.java as Class<Any>,
            V::class.java as Class<TypeViewHolder<Any>>
        )
    )
    return this
}