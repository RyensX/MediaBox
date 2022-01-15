package com.skyd.imomoe.util.dlna.dms

import android.annotation.SuppressLint
import android.content.Context
import org.fourthline.cling.support.model.container.Container

class ContentFactory private constructor(context: Context, private val baseUrl: String) {
    private val context: Context = context.applicationContext
    fun checkBaseUrl(baseUrl: String): Boolean {
        return this.baseUrl != baseUrl
    }

    fun getContent(containerId: String?): Container {
        val result = Container()
        result.childCount = 0
        when (containerId) {
            MediaItem.ROOT_ID -> {
                // 定义音频资源
                val audioContainer = Container()
                audioContainer.id = MediaItem.AUDIO_ID
                audioContainer.parentID = MediaItem.ROOT_ID
                audioContainer.clazz = MediaItem.AUDIO_CLASS
                audioContainer.title = "Audios"
                result.addContainer(audioContainer)
                result.childCount = result.childCount + 1

                // 定义图片资源
                val imageContainer = Container()
                imageContainer.id = MediaItem.IMAGE_ID
                imageContainer.parentID = MediaItem.ROOT_ID
                imageContainer.clazz = MediaItem.IMAGE_CLASS
                imageContainer.title = "Images"
                result.addContainer(imageContainer)
                result.childCount = result.childCount + 1

                // 定义视频资源
                val videoContainer = Container()
                videoContainer.id = MediaItem.VIDEO_ID
                videoContainer.parentID = MediaItem.ROOT_ID
                videoContainer.clazz = MediaItem.VIDEO_CLASS
                videoContainer.title = "Videos"
                result.addContainer(videoContainer)
                result.childCount = result.childCount + 1
            }
            MediaItem.IMAGE_ID -> {
                val contentDao = MediaContentDao(baseUrl)
                //Get image items
                val items = contentDao.getImageItems(context)
                for (item in items) {
                    result.addItem(item)
                    result.childCount = result.childCount + 1
                }
            }
            MediaItem.AUDIO_ID -> {
                val contentDao = MediaContentDao(baseUrl)
                //Get audio items
                val items = contentDao.getAudioItems(context)
                for (item in items) {
                    result.addItem(item)
                    result.childCount = result.childCount + 1
                }
            }
            MediaItem.VIDEO_ID -> {
                val contentDao = MediaContentDao(baseUrl)
                //Get video items
                val items = contentDao.getVideoItems(context)
                for (item in items) {
                    result.addItem(item)
                    result.childCount = result.childCount + 1
                }
            }
        }
        return result
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: ContentFactory
            private set

        @JvmStatic
        fun initInstance(context: Context, baseUrl: String) {
            if (!this::instance.isInitialized || instance.checkBaseUrl(baseUrl)) {
                instance = ContentFactory(context.applicationContext, baseUrl)
            }
        }
    }
}