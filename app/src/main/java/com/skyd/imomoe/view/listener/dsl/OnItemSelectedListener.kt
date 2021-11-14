package com.skyd.imomoe.view.listener.dsl

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

fun AppCompatSpinner.setOnItemSelectedListener(init: OnItemSelectedListener.() -> Unit) {
    val listener = OnItemSelectedListener()
    listener.init()
    this.onItemSelectedListener = listener
}

private typealias ItemSelected = (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
private typealias NothingSelected = (parent: AdapterView<*>?) -> Unit

class OnItemSelectedListener : AdapterView.OnItemSelectedListener {
    private var itemSelected: ItemSelected? = null
    private var nothingSelected: NothingSelected? = null

    fun onItemSelected(itemSelected: ItemSelected?) {
        this.itemSelected = itemSelected
    }

    fun onNothingSelected(nothingSelected: NothingSelected?) {
        this.nothingSelected = nothingSelected
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemSelected?.invoke(parent, view, position, id)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        nothingSelected?.invoke(parent)
    }
}
