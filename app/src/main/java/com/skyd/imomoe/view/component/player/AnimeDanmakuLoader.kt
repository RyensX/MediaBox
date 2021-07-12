package com.skyd.imomoe.view.component.player

import android.net.Uri
import master.flame.danmaku.danmaku.loader.ILoader
import master.flame.danmaku.danmaku.loader.IllegalDataException
import java.io.InputStream
import kotlin.jvm.Throws

class AnimeDanmakuLoader private constructor() : ILoader {
    private var dataSource: AnimeJSONSource? = null
    override fun getDataSource(): AnimeJSONSource? {
        return dataSource
    }

    @Throws(IllegalDataException::class)
    override fun load(uri: String) {
        dataSource = try {
            AnimeJSONSource(Uri.parse(uri))
        } catch (e: Exception) {
            throw IllegalDataException(e)
        }
    }

    @Throws(IllegalDataException::class)
    override fun load(`in`: InputStream) {
        dataSource = try {
            AnimeJSONSource(`in`)
        } catch (e: Exception) {
            throw IllegalDataException(e)
        }
    }

    companion object {
        val instance: AnimeDanmakuLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AnimeDanmakuLoader()
        }
    }
}