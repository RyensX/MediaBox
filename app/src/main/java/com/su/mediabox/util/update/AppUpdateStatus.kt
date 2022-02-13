package com.su.mediabox.util.update

enum class AppUpdateStatus {
    UNCHECK,        // 未检查
    DATED,          // 当前版本已过时，建议更新
    VALID,          // 当前是最新版本
    CHECKING,       // 检查更新中
    LATER,          // 用户选择暂不更新
    ERROR           // 任何时候出错，建议RECHECK，但可能是客户端或服务端的网络问题
}