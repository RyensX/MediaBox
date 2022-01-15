package com.skyd.imomoe.util.dlna.dmc.control

import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo

interface IServiceAction {
    enum class ServiceAction(var action: String) {
        CAST("cast"),
        PLAY("play"),
        PAUSE("pause"),
        STOP("stop"),
        SEEK_TO("seekTo"),
        SET_VOLUME("setVolume"),
        SET_MUTE("setMute"),
        SET_BRIGHTNESS("setBrightness");
    }

    interface IServiceActionCallback<T> {
        fun onSuccess(result: T)
        fun onFailed(errMsg: String)
    }

    // --------------------------------------------------------------------------------
    // ---- AvService
    // --------------------------------------------------------------------------------
    interface IAVServiceAction {
        fun cast(listener: IServiceActionCallback<String>?, uri: String, metadata: String)
        fun play(listener: IServiceActionCallback<Void?>?)
        fun pause(listener: IServiceActionCallback<Void?>?)
        fun stop(listener: IServiceActionCallback<Void?>?)
        fun seek(listener: IServiceActionCallback<Long>?, position: Long)
        fun getPositionInfo(listener: IServiceActionCallback<PositionInfo?>?)
        fun getMediaInfo(listener: IServiceActionCallback<MediaInfo?>?)
        fun getTransportInfo(listener: IServiceActionCallback<TransportInfo?>?)
    }

    // --------------------------------------------------------------------------------
    // ---- RendererService
    // --------------------------------------------------------------------------------
    interface IRendererServiceAction {
        fun setVolume(listener: IServiceActionCallback<Int>?, volume: Int)
        fun getVolume(listener: IServiceActionCallback<Int>?)
        fun setMute(listener: IServiceActionCallback<Boolean>?, mute: Boolean)
        fun isMute(listener: IServiceActionCallback<Boolean>?)
        fun setBrightness(listener: IServiceActionCallback<Int>?, percent: Int)
        fun getBrightness(listener: IServiceActionCallback<Int>?)
    }
}
