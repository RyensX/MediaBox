package com.su.mediabox.util.eventbus

interface MessageEvent

class RefreshEvent : MessageEvent

@Deprecated("更新2.0后删除")
// 根据actionUrl选择HomeFragment页面的某个Tab
class SelectHomeTabEvent(var actionUrl: String) : MessageEvent