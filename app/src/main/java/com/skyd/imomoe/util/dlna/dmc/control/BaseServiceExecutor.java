package com.skyd.imomoe.util.dlna.dmc.control;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.skyd.imomoe.util.dlna.dmc.Utils;
import com.skyd.imomoe.util.dlna.dmc.action.GetBrightness;
import com.skyd.imomoe.util.dlna.dmc.action.SetBrightness;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;

/**
 *
 */
abstract class BaseServiceExecutor {
    private final ControlPoint mControlPoint;
    private final Service<?, ?> mService;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    protected BaseServiceExecutor(ControlPoint controlPoint, Service<?, ?> service) {
        mControlPoint = controlPoint;
        mService = service;
    }

    protected Service<?, ?> getService() {
        return mService;
    }

    protected boolean invalidServiceAction(String actionName) {
        return mService == null || mService.getAction(actionName) == null;
    }

    protected void execute(ActionCallback actionCallback) {
        mControlPoint.execute(actionCallback);
    }

    protected final <T> void notifySuccess(@Nullable IServiceAction.IServiceActionCallback<T> listener, T t) {
        if (listener != null) notify(() -> listener.onSuccess(t));
    }

    protected final void notifyFailure(@Nullable IServiceAction.IServiceActionCallback<?> listener, String errMsg) {
        if (listener != null) notify(() -> listener.onFailed(errMsg != null ? errMsg : "error"));
    }

    private void notify(Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // Implement
    // ---------------------------------------------------------------------------------------------------------
    static final class AVServiceExecutorImpl extends BaseServiceExecutor implements IServiceAction.IAVServiceAction {

        public AVServiceExecutorImpl(ControlPoint controlPoint, Service<?, ?> service) {
            super(controlPoint, service);
        }

        @Override
        public void cast(IServiceAction.IServiceActionCallback<String> listener, String uri, String metadata) {
            if (invalidServiceAction("SetAVTransportURI")) return;

            execute(new SetAVTransportURI(getService(), uri, metadata) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, uri);
                }

                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void play(IServiceAction.IServiceActionCallback<Void> listener) {
            if (invalidServiceAction("Play")) return;

            execute(new Play(getService()) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, null);
                }

                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void pause(IServiceAction.IServiceActionCallback<Void> listener) {
            if (invalidServiceAction("Pause")) return;

            execute(new Pause(getService()) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, null);
                }

                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void stop(IServiceAction.IServiceActionCallback<Void> listener) {
            if (invalidServiceAction("Stop")) return;

            execute(new Stop(getService()) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, null);
                }

                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void seek(IServiceAction.IServiceActionCallback<Long> listener, long position) {
            if (invalidServiceAction("Seek")) return;

            execute(new Seek(getService(), Utils.getStringTime(position)) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, position);
                }

                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void getPositionInfo(IServiceAction.IServiceActionCallback<PositionInfo> listener) {
            if (invalidServiceAction("GetPositionInfo")) return;

            execute(new GetPositionInfo(getService()) {
                @Override
                public void received(ActionInvocation invocation, final PositionInfo positionInfo) {
                    notifySuccess(listener, positionInfo);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void getMediaInfo(IServiceAction.IServiceActionCallback<MediaInfo> listener) {
            if (invalidServiceAction("GetMediaInfo")) return;

            execute(new GetMediaInfo(getService()) {
                @Override
                public void received(ActionInvocation invocation, final MediaInfo mediaInfo) {
                    notifySuccess(listener, mediaInfo);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void getTransportInfo(IServiceAction.IServiceActionCallback<TransportInfo> listener) {
            if (invalidServiceAction("GetTransportInfo")) return;

            execute(new GetTransportInfo(getService()) {
                @Override
                public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                    notifySuccess(listener, transportInfo);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // Implement
    // ---------------------------------------------------------------------------------------------------------
    static class RendererServiceExecutorImpl extends BaseServiceExecutor implements IServiceAction.IRendererServiceAction {

        public RendererServiceExecutorImpl(ControlPoint controlPoint, Service<?, ?> service) {
            super(controlPoint, service);
        }

        @Override
        public void setVolume(IServiceAction.IServiceActionCallback<Integer> listener, int volume) {
            if (invalidServiceAction("SetVolume")) return;

            execute(new SetVolume(getService(), volume) {
                @Override
                public void success(ActionInvocation invocation) {
                    notifySuccess(listener, volume);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void getVolume(IServiceAction.IServiceActionCallback<Integer> listener) {
            if (invalidServiceAction("GetVolume")) return;

            execute(new GetVolume(getService()) {
                @Override
                public void received(ActionInvocation invocation, final int currentVolume) {
                    notifySuccess(listener, currentVolume);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void setMute(IServiceAction.IServiceActionCallback<Boolean> listener, boolean mute) {
            if (invalidServiceAction("SetMute")) return;

            execute(new SetMute(getService(), mute) {
                @Override
                public void success(ActionInvocation invocation) {
                    notifySuccess(listener, mute);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void isMute(IServiceAction.IServiceActionCallback<Boolean> listener) {
            if (invalidServiceAction("GetMute")) return;

            execute(new GetMute(getService()) {
                @Override
                public void received(ActionInvocation invocation, final boolean currentMute) {
                    notifySuccess(listener, currentMute);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void setBrightness(IServiceAction.IServiceActionCallback<Integer> listener, int percent) {
            if (invalidServiceAction("SetBrightness")) return;

            execute(new SetBrightness(getService(), percent) {
                @Override
                public void success(final ActionInvocation invocation) {
                    notifySuccess(listener, percent);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }

        @Override
        public void getBrightness(IServiceAction.IServiceActionCallback<Integer> listener) {
            if (invalidServiceAction("GetBrightness")) return;

            execute(new GetBrightness(getService()) {
                @Override
                public void received(ActionInvocation<?> invocation, final int brightness) {
                    notifySuccess(listener, brightness);
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    notifyFailure(listener, defaultMsg);
                }
            });
        }
    }
}
