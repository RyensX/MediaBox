package com.skyd.imomoe.view.activity

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinBean
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.databinding.ActivitySkinBinding
import com.skyd.imomoe.util.Util.getDefaultResColor
import com.skyd.imomoe.view.adapter.SkinAdapter
import com.skyd.imomoe.view.adapter.decoration.SkinItemDecoration
import com.skyd.imomoe.view.adapter.spansize.SkinSpanSize
import com.skyd.skin.core.SkinResourceProcessor

class SkinActivity : BaseActivity<ActivitySkinBinding>() {
    private val list: MutableList<SkinBean> = ArrayList()
    private val adapter: SkinAdapter = SkinAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSkinData()
        mBinding.run {
            llSkinActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }
            llSkinActivityToolbar.tvToolbar1Title.text = getString(R.string.skin_center)

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
                "粉色少女",
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
                "deep♂dark♂fantasy",
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
                "深蓝♂幻想",
                usingSkin("", "_blue"),
                "",
                "_blue"
            )
        )
    }
}