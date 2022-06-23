package com.su.mediabox.bean

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.su.mediabox.R
import com.su.mediabox.pluginapi.util.UIUtil.dp

class DefaultEmpty(
    @DrawableRes val icon: Int = R.drawable.ic_empty,
    @StringRes val msg: Int = R.string.empty_default_msg,
    val iconSize: Int = 64.dp
)