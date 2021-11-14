package com.skyd.imomoe.view.listener.dsl

import com.google.android.material.tabs.TabLayout

fun TabLayout.addOnTabSelectedListener(init: OnTabSelectedListener.() -> Unit) {
    val listener = OnTabSelectedListener()
    listener.init()
    this.addOnTabSelectedListener(listener)
}

private typealias TabSelected = (tab: TabLayout.Tab?) -> Unit
private typealias TabUnselected = (tab: TabLayout.Tab?) -> Unit
private typealias TabReselected = (tab: TabLayout.Tab?) -> Unit

class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
    private var tabSelected: TabSelected? = null
    private var tabUnselected: TabUnselected? = null
    private var tabReselected: TabReselected? = null

    fun onTabSelected(tabSelected: TabSelected?) {
        this.tabSelected = tabSelected
    }

    fun onTabUnselected(tabUnselected: TabUnselected?) {
        this.tabUnselected = tabUnselected
    }

    fun onTabReselected(tabReselected: TabReselected?) {
        this.tabReselected = tabReselected
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabSelected?.invoke(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tabUnselected?.invoke(tab)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        tabReselected?.invoke(tab)
    }
}
