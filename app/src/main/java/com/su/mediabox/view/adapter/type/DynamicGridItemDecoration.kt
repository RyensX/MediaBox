package com.su.mediabox.view.adapter.type

import android.graphics.Rect
import com.su.mediabox.util.logD
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.Util

/**
 * 只有使用[GridLayoutManager]才有效
 *
 * 根据spanSize和spanCount自动计算每个item需要附加的间隔
 *
 * 注意：边距和间隔不相等时可能会导致右侧View空间被挤占
 */
class DynamicGridItemDecoration(
    var itemSpacing: Int = 8.dp,
    private val hasLeftRightEdge: Boolean = true,
    private val hasTopBottomEdge: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        Util.withoutExceptionGet { parent.layoutManager as GridLayoutManager }?.apply {
            //提前重置
            outRect.set(0, 0, 0, itemSpacing)
            //开始计算
            val adapter = parent.adapter ?: return
            val position = parent.getChildAdapterPosition(view)
            val itemType = adapter.getItemViewType(position)

            val lp = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = lp.spanIndex
            val spanSize = lp.spanSize
            val spanCount = spanCount

            //第一行才会加top
            if (hasTopBottomEdge && spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0)
                outRect.top = itemSpacing

            val prePos = if (position > 0) position - 1 else -1
            val nextPos = if (position < adapter.itemCount - 1) position + 1 else -1
            //上一行的最后一个位置
            val preRowPos = if (position > spanIndex) position - (1 + spanIndex) else -1
            //下一行的第一个位置
            val nextRowPos =
                if (position < adapter.itemCount - (spanCount - spanIndex)) position + (spanCount - spanIndex) - spanSize + 1 else -1
            val isFirstRowOrColumn = position == 0 || prePos == -1 ||
                    itemType != adapter.getItemViewType(prePos) || preRowPos == -1 ||
                    itemType != adapter.getItemViewType(preRowPos)
            val isLastRowOrColumn = position == adapter.itemCount - 1 || nextPos == -1 ||
                    itemType != adapter.getItemViewType(nextPos) || nextRowPos == -1 ||
                    itemType != adapter.getItemViewType(nextRowPos)
            outRect.apply {
                if (orientation == GridLayoutManager.VERTICAL) {
                    if (hasLeftRightEdge) {
                        left = itemSpacing * (spanCount - spanIndex) / spanCount
                        right = itemSpacing * (spanIndex + (spanSize - 1) + 1) / spanCount
                    } else {
                        left = itemSpacing * spanIndex / spanCount
                        right =
                            itemSpacing * (spanCount - (spanIndex + spanSize - 1) - 1) / spanCount
                    }

                    if (isFirstRowOrColumn) {
                        if (hasTopBottomEdge)
                            bottom = itemSpacing
                    } else
                        bottom = itemSpacing
                } else {

                    if (hasTopBottomEdge) {
                        top = itemSpacing * (spanCount - spanIndex) / spanCount
                        bottom = itemSpacing * (spanIndex + (spanSize - 1) + 1) / spanCount
                    } else {
                        top = itemSpacing * spanIndex / spanCount
                        bottom =
                            itemSpacing * (spanCount - (spanIndex + spanSize - 1) - 1) / spanCount
                    }

                    if (isFirstRowOrColumn && hasLeftRightEdge)
                        left = itemSpacing

                    if (isLastRowOrColumn) {
                        if (hasLeftRightEdge)
                            right = itemSpacing
                    } else
                        right = itemSpacing
                }
            }
        } ?: super.getItemOffsets(outRect, view, parent, state)

    }

}