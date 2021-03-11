package com.skyd.imomoe.util.eventbus

interface EventBusSubscriber {
    fun onMessageEvent(event: MessageEvent)
}
