package com.su.mediabox.view.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.R
import com.su.mediabox.bean.SkinBean
import com.su.mediabox.plugin.Constant.ViewHolderTypeString
import com.su.mediabox.databinding.ActivitySkinBinding
import com.su.mediabox.util.Util.getDefaultResColor
import com.su.mediabox.view.adapter.SkinAdapter
import com.su.mediabox.view.adapter.decoration.SkinItemDecoration
import com.su.mediabox.view.adapter.spansize.SkinSpanSize
import com.su.skin.core.SkinResourceProcessor

class SkinActivity : BasePluginActivity<ActivitySkinBinding>() {
    private val list: MutableList<SkinBean> = ArrayList()
    private val adapter: SkinAdapter = SkinAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSkinData()
        mBinding.run {
            atbSkinActivityToolbar.setBackButtonClickListener { finish() }

            rvSkinActivity.layoutManager = GridLayoutManager(this@SkinActivity, 3)
                .apply { spanSizeLookup = SkinSpanSize(adapter) }
            rvSkinActivity.addItemDecoration(SkinItemDecoration())
            rvSkinActivity.adapter = adapter
        }
    }

    override fun getBinding(): ActivitySkinBinding = ActivitySkinBinding.inflate(layoutInflater)

    private fun usingSkin(skinPath: String, skinSuffix: String): Boolean {
        return SkinResourceProcessor.instance.skinPath == skinPath &&
                SkinResourceProcessor.instance.skinSuffix == skinSuffix
    }

    override fun onChangeSkin() {
        super.onChangeSkin()
        initSkinData()
        adapter.notifyDataSetChanged()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initSkinData()
        adapter.notifyDataSetChanged()
    }

    private fun initSkinData() {
        list.clear()
        list.add(
            SkinBean(
                ViewHolderTypeString.SKIN_COVER_1,
                "",
                getDefaultResColor(R.color.main_color_2_skin),
                "粉色少女🎀",
                usingSkin("", ""),
                "",
                ""
            )
        )
        list.add(
            SkinBean(
                ViewHolderTypeString.SKIN_COVER_1,
                "",
                getDefaultResColor(R.color.black),
                "deep♂️dark♂️fantasy",
                usingSkin("", "_dark"),
                "",
                "_dark"
            )
        )
        list.add(
            SkinBean(
                ViewHolderTypeString.SKIN_COVER_1,
                "",
                getDefaultResColor(R.color.main_color_2_skin_blue),
                "♂️深蓝幻想",
                usingSkin("", "_blue"),
                "",
                "_blue"
            )
        )
        list.add(
            SkinBean(
                ViewHolderTypeString.SKIN_COVER_1,
                "",
                getDefaultResColor(R.color.main_color_2_skin_lemon),
                "柠檬酸🍋",
                usingSkin("", "_lemon"),
                "",
                "_lemon"
            )
        )
        list.add(
            SkinBean(
                ViewHolderTypeString.SKIN_COVER_1,
                "",
                getDefaultResColor(R.color.main_color_2_skin_sweat_soybean),
                "流汗黄豆😅",
                usingSkin("", "_sweat_soybean"),
                "",
                "_sweat_soybean"
            )
        )
    }
}