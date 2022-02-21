package com.su.mediabox.util.downloadanime

interface DownloadListener {
    fun complete(fileName: String)

    fun error() {
    }
}