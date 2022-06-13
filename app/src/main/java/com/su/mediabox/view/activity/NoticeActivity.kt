package com.su.mediabox.view.activity

import android.os.Bundle
import android.text.Html
import com.su.mediabox.databinding.ActivityNoticeBinding
import com.su.mediabox.util.viewBind
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class NoticeActivity : BasePluginActivity() {
    companion object {
        const val PARAM = "param"
        const val TOOLBAR_TITLE = "toolbarTitle"
        const val TITLE = "title"
        const val CONTENT = "content"
    }

    private val mBinding by viewBind(ActivityNoticeBinding::inflate)
    private val paramMap: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (intent.getStringExtra(PARAM) ?: "").split("&").forEachIndexed { index, s ->
            s.split("=").let {
                if (it.size != 2) return@let
                try {
                    // 此处URL解码，因此要求传入的参数需要经过URL编码！！！
                    paramMap[it[0]] = URLDecoder.decode(it[1], "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }

        mBinding.run {
            atbNoticeActivityToolbar.run {
                setBackButtonClickListener { finish() }
                titleText = paramMap[TOOLBAR_TITLE] ?: "通知"
            }
            tvNoticeActivityTitle.text = paramMap[TITLE] ?: ""
            tvNoticeActivityContent.text = Html.fromHtml(paramMap[CONTENT] ?: "")
        }
    }

}