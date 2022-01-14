package com.skyd.imomoe.util.dlna.dms

import android.content.Context
import org.fourthline.cling.support.model.item.Item

internal interface IMediaContentDao {
    fun getImageItems(context: Context): List<Item?>
    fun getAudioItems(context: Context): List<Item?>
    fun getVideoItems(context: Context): List<Item?>
}