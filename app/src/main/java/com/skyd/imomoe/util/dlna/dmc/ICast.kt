package com.skyd.imomoe.util.dlna.dmc

interface ICast {
    val id: String
    val uri: String
    val name: String?

    interface ICastVideo : ICast {
        /**
         * @return video duration, ms
         */
        val durationMillSeconds: Long
        val size: Long
        val bitrate: Long
    }

    interface ICastAudio : ICast {
        /**
         * @return audio duration, ms
         */
        val durationMillSeconds: Long
        val size: Long
    }

    interface ICastImage : ICast {
        val size: Long
    }
}