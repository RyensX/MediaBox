package com.su.mediabox.util.eventbus

interface EventBusSubscriber {
    fun onMessageEvent(event: MessageEvent)
}
