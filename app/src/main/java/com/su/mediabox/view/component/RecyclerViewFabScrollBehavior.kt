package com.su.mediabox.view.component

import android.content.Context
import android.util.AttributeSet
import com.su.mediabox.util.logD
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * 上滑显示，下滑隐藏
 */
class RecyclerViewFabScrollBehavior(context: Context, attr: AttributeSet) :
    FloatingActionButton.Behavior(context, attr) {

    private val fabOnVisibilityChangedListener =
        object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                fab?.visibility = View.INVISIBLE
            }
        }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ) = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child, target,
            dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed,
            type, consumed
        )
        if (dyConsumed > 0) {
            //下滑
            child.hide(fabOnVisibilityChangedListener)
        } else if (dyConsumed < 0) {
            //上滑
            child.show()
        }
    }

}