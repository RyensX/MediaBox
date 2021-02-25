package com.skyd.imomoe.util.dlna.dmc.control;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

public interface IServiceAction {

    enum ServiceAction {
        CAST("cast"),
        PLAY("play"),
        PAUSE("pause"),
        STOP("stop"),
        SEEK_TO("seekTo"),
        SET_VOLUME("setVolume"),
        SET_MUTE("setMute"),
        SET_BRIGHTNESS("setBrightness");

        String action;

        ServiceAction(String action) {
            this.action = action;
        }
    }

    interface IServiceActionCallback<T> {
        void onSuccess(T result);

        void onFailed(String errMsg);
    }

    // --------------------------------------------------------------------------------
    // ---- AvService
    // --------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    interface IAVServiceAction {

        void cast(IServiceActionCallback<String> listener, String uri, String metadata);

        void play(IServiceActionCallback<Void> listener);

        void pause(IServiceActionCallback<Void> listener);

        void stop(IServiceActionCallback<Void> listener);

        void seek(IServiceActionCallback<Long> listener, final long position);

        void getPositionInfo(IServiceActionCallback<PositionInfo> listener);

        void getMediaInfo(IServiceActionCallback<MediaInfo> listener);

        void getTransportInfo(IServiceActionCallback<TransportInfo> listener);
    }

    // --------------------------------------------------------------------------------
    // ---- RendererService
    // --------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    interface IRendererServiceAction {
        void setVolume(IServiceActionCallback<Integer> listener, final int volume);

        void getVolume(IServiceActionCallback<Integer> listener);

        void setMute(IServiceActionCallback<Boolean> listener, boolean mute);

        void isMute(IServiceActionCallback<Boolean> listener);

        void setBrightness(IServiceActionCallback<Integer> listener, final int percent);

        void getBrightness(IServiceActionCallback<Integer> listener);
    }
}
