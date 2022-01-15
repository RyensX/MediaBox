package com.skyd.imomoe.util.dlna.dmc.action

import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes

abstract class SetBrightness(service: Service<*, *>?, newBrightness: Long) :
    ActionCallback(ActionInvocation(service?.getAction("SetBrightness"))) {

    override fun success(invocation: ActionInvocation<out Service<*, *>>?) {}

    init {
        super.getActionInvocation().setInput("InstanceID", UnsignedIntegerFourBytes(0))
        //getActionInvocation().setInput("Channel", Channel.Master.toString());
        super.getActionInvocation()
            .setInput("DesiredBrightness", UnsignedIntegerTwoBytes(newBrightness))
    }
}