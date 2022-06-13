package com.su.mediabox.view.adapter.type

import android.content.Context
import android.content.MutableContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.util.geMemberOrNull

/**
 * 继承本类的任何子类均要求实现一个只接收ViewGroup的构造方法，否则无法正确创建而创建为[UnknownTypeViewHolder]
 */
abstract class TypeViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    //因为复用问题，这两项都不能缓存
    val mOwnerRecyclerView: RecyclerView?
        get() = RecyclerView.ViewHolder::class.java.geMemberOrNull("mOwnerRecyclerView", this)
    val bindingTypeAdapter: TypeAdapter
        get() = bindingAdapter as TypeAdapter

    //由于存在跨页面的全局复用池所以必须自己提供正确的context
    private val bindingContextWrapper = MutableContextWrapper(view.context)

    //VH内都必须只使用这个context防止错乱
    val bindingContext: Context = bindingContextWrapper

    fun checkBindingContext(context: Context) {
        if (bindingContextWrapper.baseContext != context)
            bindingContextWrapper.baseContext = context
    }

    open fun onBind(data: T) {
        if (data is BaseData) {
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