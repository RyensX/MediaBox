package com.su.mediabox.plugin

import java.net.URLDecoder

@Deprecated("使用新的Action动作数据代替")
object AppRouteProcessor {

    @Deprecated("更新2.0后删除")
    inline fun matchAndGetParams(
        actionUrl: String,
        action: String,
        matchSuccess: (List<String>) -> Unit
    ) =
        try {
            if (actionUrl.startsWith(action)) {
                actionUrl.removePrefix(action)
                    .removePrefix("/")
                    .removeSuffix("/")
                    .split("/")
                    .also { list ->
                        //可能部分路由需要把actionUrl作为参数，所以在判断和分割参数后再解码
                        matchSuccess(list.map { URLDecoder.decode(it, "UTF-8") })
                    }
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
}