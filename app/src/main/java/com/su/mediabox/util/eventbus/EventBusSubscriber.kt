package com.su.mediabox.util.eventbus

@Deprecated("更新2.0后删除")
interface EventBusSubscriber {
    fun onMessageEvent(event: MessageEvent)
}
