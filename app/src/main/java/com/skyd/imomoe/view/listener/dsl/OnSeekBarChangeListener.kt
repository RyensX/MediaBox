package com.skyd.imomoe.view.listener.dsl

import android.widget.SeekBar

fun SeekBar.setOnSeekBarChangeListener(init: OnSeekBarChangeListener.() -> Unit) {
    val listener = OnSeekBarChangeListener()
    listener.init()
    this.setOnSeekBarChangeListener(listener)
}

private typealias ProgressChanged = (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit
private typealias StartTrackingTouch = (seekBar: SeekBar?) -> Unit
private typealias StopTrackingTouch = (seekBar: SeekBar?) -> Unit

class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    private var progressChanged: ProgressChanged? = null
    private var startTrackingTouch: StartTrackingTouch? = null
    private var stopTrackingTouch: StopTrackingTouch? = null

    fun onProgressChanged(progressChanged: ProgressChanged?) {
        this.progressChanged = progressChanged
    }

    fun onStartTrackingTouch(startTrackingTouch: StartTrackingTouch?) {
        this.startTrackingTouch = startTrackingTouch
    }

    fun onStopTrackingTouch(stopTrackingTouch: StopTrackingTouch?) {
        this.stopTrackingTouch = stopTrackingTouch
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        progressChanged?.invoke(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        startTrackingTouch?.invoke(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        stopTrackingTouch?.invoke(seekBar)
    }
}
