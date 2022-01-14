package com.skyd.imomoe.util.dlna

import com.skyd.imomoe.util.dlna.dmc.ICast
import com.skyd.imomoe.util.dlna.dmc.ICast.ICastVideo

object CastObject {
    fun newInstance(url: String, id: String, name: String?): ICast? {
        return when {
            url.endsWith(".mp4") -> CastVideo.newInstance(url, id, name)
            url.endsWith(".mp3") -> CastAudio.newInstance(url, id, name)
            url.endsWith(".jpg") -> CastImage.newInstance(url, id, name)
            else -> null
        }
    }

    class CastAudio(
        override val uri: String,
        override val id: String,
        override val name: String?
    ) : ICastVideo {
        override var durationMillSeconds: Long = 0
            private set

        /**
         * @param duration the total time of video (ms)
         */
        fun setDuration(duration: Long): CastAudio {
            durationMillSeconds = duration
            return this
        }

        override val size: Long
            get() = 0
        override val bitrate: Long
            get() = 0

        companion object {
            @JvmStatic
            fun newInstance(url: String, id: String, name: String?): CastAudio {
                return CastAudio(url, id, name)
            }
        }
    }

    class CastImage(
        override val uri: String,
        override val id: String,
        override val name: String?
    ) : ICastVideo {
        override val durationMillSeconds: Long
            get() = -1L
        override val size: Long
            get() = 0
        override val bitrate: Long
            get() = 0

        companion object {
            @JvmStatic
            fun newInstance(url: String, id: String, name: String?): CastImage {
                return CastImage(url, id, name)
            }
        }
    }

    class CastVideo(override val uri: String, override val id: String, override val name: String?) :
        ICastVideo {
        override var durationMillSeconds: Long = 0
            private set

        /**
         * @param duration the total time of video (ms)
         */
        fun setDuration(duration: Long): CastVideo {
            durationMillSeconds = duration
            return this
        }

        override val size: Long
            get() = 0
        override val bitrate: Long
            get() = 0

        companion object {
            @JvmStatic
            fun newInstance(url: String, id: String, name: String?): CastVideo {
                return CastVideo(url, id, name)
            }
        }
    }
}