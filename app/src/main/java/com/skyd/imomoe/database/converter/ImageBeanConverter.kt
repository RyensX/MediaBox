package com.skyd.imomoe.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.su.mediabox.plugin.standard.been.ImageBean


class ImageBeanConverter {

    @TypeConverter
    fun string2ImageBean(s: String?): ImageBean? = Gson().fromJson(s, ImageBean::class.java)

    @TypeConverter
    fun imageBean2String(imageBean: ImageBean?): String? = Gson().toJson(imageBean)

}
