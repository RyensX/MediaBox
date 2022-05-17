package com.su.mediabox.view.component.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.su.mediabox.R

class CenterAlignedBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attributeSet) {

    private var targetViewId: Int = -1

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.CoordinatorLayout_Layout).apply {
            targetViewId =
                getResourceId(R.styleable.CoordinatorLayout_Layout_alignTargetView, targetViewId)
            recycle()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View) =
        dependency.id == targetViewId

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.x = dependency.x - (child.width - dependency.width) / 2f
        child.y = dependency.y - (child.height - dependency.height) / 2f
        return true
    }
}