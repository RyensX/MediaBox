package com.skyd.imomoe.util.update

enum class AppUpdateStatus {
    UNCHECK,        // 未检查
    DATED,          // 当前版本已过时，建议更新
    VALID,          // 当前是最新版本
    CHECKING,       // 检查更新中
    LATER,          // 用户选择暂不更新
    DOWNLOADING,    // 正在下载更新
    CANCEL,         // 取消下载
    TO_BE_INSTALLED,// 安装包准备完毕，等待安装
    ERROR           // 任何时候出错，建议RECHECK，但可能是客户端或服务端的网络问题
}