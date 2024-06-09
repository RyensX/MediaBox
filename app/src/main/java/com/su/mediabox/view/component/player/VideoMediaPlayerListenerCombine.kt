package com.su.mediabox.view.component.player

import com.su.mediabox.pluginapi.data.VideoPlayMedia


/**
 *
 * Created by Ryens.
 * https://github.com/RyensX
 */
class VideoMediaPlayerListenerCombine : VideoMediaPlayerListener {

    private val listenerList = mutableListOf<VideoMediaPlayerListener>()

    init {

    }

    fun add(listener: VideoMediaPlayerListener) {
        listenerList.add(listener)
    }

    fun remove(listener: VideoMediaPlayerListener) {
        listenerList.remove(listener)
    }

    fun clear() {
        listenerList.clear()
    }

    override fun onClickBlank() {
        listenerList.forEach {
            it.onClickBlank()
        }
    }

    override fun onPlayProgressUpdate(progress: Int) {
        listenerList.forEach {
            it.onPlayProgressUpdate(progress)
        }
    }

    override fun onUpdateVideoMedia(videoName: String, episodeName: String, url: String) {
        listenerList.forEach {
            it.onUpdateVideoMedia(videoName, episodeName, url)
        }
    }
}