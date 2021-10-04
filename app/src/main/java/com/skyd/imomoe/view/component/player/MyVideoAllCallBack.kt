package com.skyd.imomoe.view.component.player

import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack

interface MyVideoAllCallBack : VideoAllCallBack {
    fun onVideoPause()

    fun onVideoResume()

    fun onVideoSizeChanged()
}