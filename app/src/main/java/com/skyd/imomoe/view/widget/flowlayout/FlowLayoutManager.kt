package com.skyd.imomoe.view.widget.flowlayout

import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


class FlowLayoutManager(val vSpace: Int, val hSpace: Int, val recyclerView: RecyclerView) :
    RecyclerView.LayoutManager() {
    private var mTotalHeight: Int = 0
    private var mVerticalScrollOffset = 0

    //item的位置信息
    private val mAllItemRect: SparseArray<Rect> = SparseArray<Rect>()

    //item是否处于可见
    private val mItemState = SparseBooleanArray()

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }
        detachAndScrapAttachedViews(recycler)

        var offsetY = paddingTop
        var offsetX = paddingStart
        var w = 0
        var h = 0
        for (i in 0 until itemCount) {
            val view: View = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            calculateItemDecorationsForChild(view, Rect())
            w = getDecoratedMeasuredWidth(view)
            h = getDecoratedMeasuredHeight(view)
            //换行
            if (offsetX + w + hSpace > getHorizontalSpace() + paddingStart) {
                if (offsetY == paddingTop) {
                    offsetY += h
                } else {
                    offsetY += (h + vSpace)
                }
                val left: Int = paddingStart
                val right: Int = paddingStart + w
                val top: Int = offsetY + vSpace
                val bottom: Int = offsetY + h + vSpace
                offsetX = right
                layoutDecorated(view, left, top, right, bottom)
                mAllItemRect.put(i, Rect(left, top, right, bottom))
            } else {
                var top: Int
                var bottom: Int
                var left: Int
                var right: Int
                if (offsetX == paddingStart) {
                    left = offsetX
                    right = offsetX + w
                    offsetX += w
                } else {
                    left = offsetX + hSpace
                    right = offsetX + w + hSpace
                    offsetX += (w + hSpace)
                }
                if (offsetY == paddingTop) {
                    top = offsetY
                    bottom = offsetY + h
                } else {
                    top = offsetY + vSpace
                    bottom = offsetY + h + vSpace
                }
                layoutDecorated(view, left, top, right, bottom)
                mAllItemRect.put(i, Rect(left, top, right, bottom))
            }
            mItemState.put(i, false)
        }
        mTotalHeight += (offsetY + h + vSpace)

//        recycleAndFillView(
//            recycler, state, Rect(
//                paddingStart,
//                mVerticalScrollOffset + paddingTop,
//                getHorizontalSpace() + paddingStart,
//                mVerticalScrollOffset + getVerticalSpace() + paddingTop
//            )
//        )
    }

    private fun recycleAndFillView(
        recycler: Recycler, state: RecyclerView.State, visibleRect: Rect? = null

    ) {
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }

        detachAndScrapAttachedViews(recycler)

        val childCount = childCount
        Log.e("handleRecycle", childCount.toString() + "")
        val visRect = visibleRect ?: Rect()
        if (visibleRect == null) {
            recyclerView.getLocalVisibleRect(visRect)
        }

        for (i in 0 until itemCount) {
            val viewForPosition = recycler.getViewForPosition(i)
            val rect: Rect = mAllItemRect.get(i)
            if (Rect.intersects(visRect, rect)) {
                addView(viewForPosition)
                measureChildWithMargins(viewForPosition, 0, 0)
                layoutDecorated(
                    viewForPosition,
                    rect.left,
                    rect.top - mVerticalScrollOffset,
                    rect.right,
                    rect.bottom - mVerticalScrollOffset
                )
            } else {
                removeAndRecycleView(viewForPosition, recycler)
            }
        }

//
//        /**
//         * 将滑出屏幕的Items回收到Recycle缓存中
//         */
//        val childRect = Rect()
//        for (i in 0 until itemCount) {
//            if (!mItemState[i]) continue        //对于已经回收的不用下面的操作
//            //这个方法获取的是RecyclerView中的View，注意区别Recycler中的View
//            //这获取的是实际的View
//            val child = recycler.getViewForPosition(i)
//            //下面几个方法能够获取每个View占用的空间的位置信息，包括ItemDecorator
//            childRect.left = getDecoratedLeft(child)
//            childRect.top = getDecoratedTop(child)
//            childRect.right = getDecoratedRight(child)
//            childRect.bottom = getDecoratedBottom(child)
//            //如果Item没有在显示区域，就说明需要回收
//            if (!Rect.intersects(displayRect, childRect)) {
//                //移除并回收掉滑出屏幕的View
//                detachAndScrapView(child, recycler)
//                removeAndRecycleView(child, recycler)
//                mItemState.put(i, false) //更新该View的状态为未依附
//            }
//        }
//
//        //重新显示需要出现在屏幕的子View
//        for (i in 0 until itemCount) {
//            if (mItemState[i]) continue       //对于已经显示的不需要下面操作
//            //判断ItemView的位置和当前显示区域是否重合
//            if (Rect.intersects(displayRect, mAllItemRect.get(i))) {
//                val view: View = recycler.getViewForPosition(i)
//                addView(view)
//                measureChildWithMargins(view, 0, 0)
//                val rect: Rect = mAllItemRect.get(i)
//                layoutDecorated(
//                    view,
//                    rect.left,
//                    rect.top - mVerticalScrollOffset,
//                    rect.right,
//                    rect.bottom - mVerticalScrollOffset
//                )
//                mItemState.put(i, true)
//            }
//        }
        Log.e("---", getChildCount().toString())
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        var deltaY = dy
        //如果滑动到最顶部
        if (mVerticalScrollOffset + dy < 0) {
            deltaY = -mVerticalScrollOffset
        } else if (mVerticalScrollOffset + dy > mTotalHeight - getVerticalSpace()) { //如果滑动到最底部
            deltaY = mTotalHeight - getVerticalSpace() - mVerticalScrollOffset
        }
        mVerticalScrollOffset += deltaY
        // 调用该方法通知view在y方向上移动指定距离
        offsetChildrenVertical(-deltaY)
        recycleAndFillView(
            recycler, state, Rect(
                paddingStart,
                mVerticalScrollOffset + paddingTop,
                getHorizontalSpace() + paddingStart,
                mVerticalScrollOffset + getVerticalSpace() + paddingTop
            )
        )
        return deltaY
    }

    private fun getVerticalSpace(): Int {
        return height - paddingBottom - paddingTop
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingStart - paddingEnd
    }
}