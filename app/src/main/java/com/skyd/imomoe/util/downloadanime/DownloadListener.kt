package com.skyd.imomoe.util.downloadanime

interface DownloadListener {
    fun complete(fileName: String)

    fun error() {
    }
}