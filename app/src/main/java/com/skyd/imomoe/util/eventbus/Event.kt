package com.skyd.imomoe.util.eventbus

interface MessageEvent

class RefreshEvent : MessageEvent

// 根据actionUrl选择HomeFragment页面的某个Tab
class SelectHomeTabEvent(var actionUrl: String) : MessageEvent