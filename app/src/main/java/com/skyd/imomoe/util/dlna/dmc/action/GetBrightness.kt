package com.skyd.imomoe.util.dlna.dmc.action

import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.model.action.ActionException
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.ErrorCode
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import java.lang.Exception

abstract class GetBrightness(service: Service<*, *>?) :
    ActionCallback(ActionInvocation(service?.getAction("GetBrightness"))) {

    override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
        invocation ?: return
        var ok = true
        var brightness = 0
        try {
            brightness = invocation.getOutput("CurrentBrightness").value.toString()
                .toInt() // UnsignedIntegerTwoBytes...
        } catch (ex: Exception) {
            invocation.failure = ActionException(
                ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: $ex", ex
            )
            failure(invocation, null)
            ok = false
        }
        if (ok) received(invocation, brightness)
    }

    abstract fun received(actionInvocation: ActionInvocation<out Service<*, *>>?, brightness: Int)

    init {
        super.getActionInvocation().setInput("InstanceID", UnsignedIntegerFourBytes(0))
        //getActionInvocation().setInput("Channel", Channel.Master.toString());
    }
}