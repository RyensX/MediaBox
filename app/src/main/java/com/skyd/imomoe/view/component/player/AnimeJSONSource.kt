package com.skyd.imomoe.view.component.player

import android.net.Uri
import android.text.TextUtils
import master.flame.danmaku.danmaku.parser.IDataSource
import master.flame.danmaku.danmaku.util.IOUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import kotlin.jvm.Throws

class AnimeJSONSource : IDataSource<JSONArray> {
    private var mJSONArray: JSONArray? = null
    var danmuCount: Long = 0
    var code = 0
    var msg: String? = null
    private var mInput: InputStream? = null

    constructor(json: String) {
        init(json)
    }

    constructor(`in`: InputStream?) {
        init(`in`)
    }

    @Throws(JSONException::class)
    private fun init(`in`: InputStream?) {
        if (`in` == null) throw NullPointerException("input stream cannot be null!")
        mInput = `in`
        val json = IOUtils.getString(mInput)
        init(json)
    }

    constructor(url: URL) : this(url.openStream())

    constructor(file: File) {
        init(FileInputStream(file))
    }

    constructor(uri: Uri) {
        val scheme = uri.scheme
        if (IDataSource.SCHEME_HTTP_TAG.equals(scheme, ignoreCase = true)
            || IDataSource.SCHEME_HTTPS_TAG.equals(scheme, ignoreCase = true)) {
            init(URL(uri.path).openStream())
        } else if (IDataSource.SCHEME_FILE_TAG.equals(scheme, ignoreCase = true)) {
            init(FileInputStream(uri.path))
        }
    }

    @Throws(JSONException::class)
    private fun init(json: String) {
        if (!TextUtils.isEmpty(json)) {
            val o = JSONObject(json)
            danmuCount = o.getLong("danum")
            code = o.getInt("code")
            msg = o.getString("msg")
            mJSONArray = o.getJSONArray("danmuku")
        }
    }

    override fun data(): JSONArray? {
        return mJSONArray
    }

    override fun release() {
        IOUtils.closeQuietly(mInput)
        mInput = null
        mJSONArray = null
    }

}