package com.skyd.imomoe.util.downloadanime

enum class AnimeDownloadStatus {
    DOWNLOADING,    // 正在下载更新
    COMPLETE,    // 下载完成
    CANCEL,         // 取消下载
    ERROR           // 下载失败
}