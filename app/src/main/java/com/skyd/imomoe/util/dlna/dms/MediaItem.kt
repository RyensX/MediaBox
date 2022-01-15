package com.skyd.imomoe.util.dlna.dms

import org.fourthline.cling.support.model.DIDLObject

interface MediaItem {
    companion object {
        const val ROOT_ID = "0"
        const val AUDIO_ID = "10"
        const val VIDEO_ID = "20"
        const val IMAGE_ID = "30"
        val AUDIO_CLASS = DIDLObject.Class("object.container.audio")
        val IMAGE_CLASS = DIDLObject.Class("object.item.imageItem")
        val VIDEO_CLASS = DIDLObject.Class("object.container.video")
    }
}