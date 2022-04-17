package com.su.mediabox.view.adapter.type

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.util.geMemberOrNull
import com.su.mediabox.util.getFirstItemDecorationBy

/**
 * 继承本类的任何子类均要求实现一个只接收ViewGroup的构造方法，否则无法正确创建而创建为[UnknownTypeViewHolder]
 */
abstract class TypeViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    //因为复用问题，这两项都不能缓存
    protected val mOwnerRecyclerView: RecyclerView?
        get() = RecyclerView.ViewHolder::class.java.geMemberOrNull("mOwnerRecyclerView", this)
    val bindingTypeAdapter: TypeAdapter
        get() = bindingAdapter as TypeAdapter

    open fun onBind(data: T) {
        if (data is BaseData) {
            bindingTypeAdapter.getData<BaseData>(0)?.layoutConfig?.apply {
                Log.d("检测到配置", toString())
                //spanCount
                val layoutManager = mOwnerRecyclerView?.layoutManager
                if (layoutManager is GridLayoutManager && layoutManager.spanCount != spanCount)
                    layoutManager.spanCount = spanCount

                //只有存在DynamicGridItemDecoration才生效
                mOwnerRecyclerView?.getFirstItemDecorationBy<DynamicGridItemDecoration>()
                    ?.apply {
                        //间距
                        spacing = itemSpacing
                        //边距
                        leftEdge = listLeftEdge
                        rightEdge = listRightEdge
                    }
                //边距
            }
            //padding
            itemView.apply {
                if (paddingLeft != data.paddingLeft ||
                    paddingTop != data.paddingTop ||
                    paddingRight != data.paddingRight ||
                    paddingBottom != data.paddingBottom
                )
                    setPadding(
                        data.paddingLeft,
                        data.paddingTop,
                        data.paddingRight,
                        data.paddingBottom
                    )
            }
        }
    }

    open fun onBind(data: T, payloads: MutableList<Any>) {
        onBind(data)
    }

    open fun onViewAttachedToWindow() {}
    open fun onViewDetachedFromWindow() {}

    class UnknownTypeViewHolder(parent: ViewGroup) : TypeViewHolder<Any>(parent.run {
        LayoutInflater.from(context).inflate(R.layout.item_unknown_type, parent, false)
    })
}