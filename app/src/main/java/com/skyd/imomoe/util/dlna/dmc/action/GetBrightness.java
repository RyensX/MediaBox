package com.skyd.imomoe.util.dlna.dmc.action;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 *
 */
public abstract class GetBrightness extends ActionCallback {
    @SuppressWarnings("WeakerAccess")
    public GetBrightness(Service<?, ?> service) {
        super(new ActionInvocation<>(service.getAction("GetBrightness")));
        getActionInvocation().setInput("InstanceID", new UnsignedIntegerFourBytes(0));
        //getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    public void success(ActionInvocation invocation) {
        boolean ok = true;
        int brightness = 0;
        try {
            brightness = Integer.parseInt(invocation.getOutput("CurrentBrightness").getValue().toString()); // UnsignedIntegerTwoBytes...
        } catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex));

            failure(invocation, null);

            ok = false;
        }
        if (ok) {
            received(invocation, brightness);
        }
    }

    public abstract void received(ActionInvocation<?> actionInvocation, int brightness);

}