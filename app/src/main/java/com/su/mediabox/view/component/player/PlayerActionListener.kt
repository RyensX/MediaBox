package com.su.mediabox.view.component.player


/**
 *
 * Created by Ryens.
 * https://github.com/RyensX
 */
interface PlayerActionListener {
    fun onClickBlank()

    /**
     * @param progress 当前播放毫秒（这里精准度其实只有秒）
     */
    fun onPlayProgressUpdate(progress: Int)
}