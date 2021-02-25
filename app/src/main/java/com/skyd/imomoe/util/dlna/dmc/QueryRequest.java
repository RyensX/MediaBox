package com.skyd.imomoe.util.dlna.dmc;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyd.imomoe.util.dlna.dmc.control.ICastInterface;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;

abstract class QueryRequest<T> {

    @Nullable
    private final Service<?, ?> service;
    private ICastInterface.GetInfoListener<T> listener;
    private final ILogger logger = new ILogger.DefaultLoggerImpl(this);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public QueryRequest(@Nullable Service<?, ?> service) {
        this.service = service;
    }

    @Nullable
    protected final Service<?, ?> getService() {
        return service;
    }

    protected abstract String getActionName();

    protected abstract ActionCallback getAction();

    protected void setResult(T t) {
        if (listener != null) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                mainHandler.post(() -> listener.onGetInfoResult(t, null));
            } else {
                listener.onGetInfoResult(t, null);
            }
        }
    }

    protected void setError(String errorMsg) {
        logger.e(errorMsg != null ? errorMsg : "error");
        if (listener != null) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                mainHandler.post(() -> listener.onGetInfoResult(null, errorMsg != null ? errorMsg : "error"));
            } else {
                listener.onGetInfoResult(null, errorMsg != null ? errorMsg : "error");
            }
        }
    }

    public final void execute(ControlPoint point, ICastInterface.GetInfoListener<T> listener) {
        this.listener = listener;
        if (TextUtils.isEmpty(getActionName())) {
            setError("not find action name!");
            return;
        } else if (getService() == null) {
            setError("the service is NULL!");
            return;
        } else if (getService().getAction(getActionName()) == null) {
            setError(String.format("this service not support '%s' action.", getActionName()));
            return;
        }
        ActionCallback actionCallback = getAction();
        if (actionCallback != null) {
            point.execute(getAction());
        } else {
            setError("this service action is NULL!");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---- MediaInfo
    // ---------------------------------------------------------------------------------------------
    static class MediaInfoRequest extends QueryRequest<MediaInfo> {

        public MediaInfoRequest(Service<?, ?> service) {
            super(service);
        }

        @Override
        protected String getActionName() {
            return "GetMediaInfo";
        }

        @Override
        protected ActionCallback getAction() {
            return getService() != null ?
                    new GetMediaInfo(getService()) {
                        @Override
                        public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                            setResult(mediaInfo);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            setError(defaultMsg);
                        }
                    } :
                    null;
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---- PositionInfo
    // ---------------------------------------------------------------------------------------------
    static final class PositionInfoRequest extends QueryRequest<PositionInfo> {

        public PositionInfoRequest(@NonNull Service<?, ?> service) {
            super(service);
        }

        @Override
        protected String getActionName() {
            return "GetPositionInfo";
        }

        @Override
        protected ActionCallback getAction() {
            return getService() != null ?
                    new GetPositionInfo(getService()) {
                        @Override
                        public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                            setResult(positionInfo);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            setError(defaultMsg);
                        }
                    } :
                    null;
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---- TransportInfo
    // ---------------------------------------------------------------------------------------------
    static final class TransportInfoRequest extends QueryRequest<TransportInfo> {

        public TransportInfoRequest(@NonNull Service<?, ?> service) {
            super(service);
        }

        @Override
        protected String getActionName() {
            return "GetTransportInfo";
        }

        @Override
        protected ActionCallback getAction() {
            return getService() != null ?
                    new GetTransportInfo(getService()) {
                        @Override
                        public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                            setResult(transportInfo);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            setError(defaultMsg);
                        }
                    } :
                    null;
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---- VolumeInfo
    // ---------------------------------------------------------------------------------------------
    static final class VolumeInfoRequest extends QueryRequest<Integer> {

        public VolumeInfoRequest(@NonNull Service<?, ?> service) {
            super(service);
        }

        @Override
        protected String getActionName() {
            return "GetVolume";
        }

        @Override
        protected ActionCallback getAction() {
            return getService() != null ?
                    new GetVolume(getService()) {
                        @Override
                        public void received(ActionInvocation actionInvocation, int currentVolume) {
                            setResult(currentVolume);
                        }

                        @Override
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            setError(defaultMsg);
                        }
                    } :
                    null;
        }

    }
}
