package com.su.mediabox.view.component.player

import com.su.mediabox.pluginapi.data.VideoPlayMedia


/**
 *
 * Created by Ryens.
 * https://github.com/RyensX
 */
interface VideoMediaListener {
    fun onUpdateVideoMedia(
        videoName: String, episodeName: String, url: String
    )
}