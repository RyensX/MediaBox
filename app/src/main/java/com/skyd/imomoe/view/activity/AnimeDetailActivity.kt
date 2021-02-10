package com.skyd.imomoe.view.activity

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.BlurUtils.blur
import com.skyd.imomoe.util.Util.getStatusBarHeight
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.loadImage
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.setTransparentStatusBar
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import kotlinx.android.synthetic.main.activity_anime_detail.*
import java.lang.reflect.Field


class AnimeDetailActivity : BaseActivity() {
    private var partUrl: String = ""
    private lateinit var viewModel: AnimeDetailViewModel
    private lateinit var adapter: AnimeDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        setTransparentStatusBar(window, isDark = true)

        val statusBarLinearParams = view_anime_detail_activity_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight()
        view_anime_detail_activity_status_bar.layoutParams = statusBarLinearParams

        viewModel = ViewModelProvider(this).get(AnimeDetailViewModel::class.java)
        adapter = AnimeDetailAdapter(this, viewModel.animeDetailBeanDataList)

        partUrl = intent.getStringExtra("partUrl") ?: ""

        tv_anime_detail_activity_toolbar_title.isFocused = true
        iv_anime_detail_activity_back.setOnClickListener { finish() }

        val layoutManager = LinearLayoutManager(this)
        rv_anime_detail_activity_info.layoutManager = layoutManager
        rv_anime_detail_activity_info.setHasFixedSize(true)
        rv_anime_detail_activity_info.adapter = adapter

        srl_anime_detail_activity.setOnRefreshListener { viewModel.getAnimeDetailData(partUrl) }
        srl_anime_detail_activity.setColorSchemeResources(R.color.main_color)

        tv_anime_detail_activity_type.gone()
        tv_anime_detail_activity_tag.gone()

        viewModel.mldAnimeDetailData.observe(this, {
            srl_anime_detail_activity.isRefreshing = false

            //先隐藏
            ObjectAnimator.ofFloat(iv_anime_detail_activity_background, "alpha", 1f, 0f)
                .setDuration(250).start()
            Glide.with(this).asBitmap().load(it.cover).into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bitmapDrawable = BitmapDrawable(null, blur(resource))
                    bitmapDrawable.colorFilter = PorterDuffColorFilter(
                        Color.LTGRAY, PorterDuff.Mode.MULTIPLY
                    )
                    Glide.with(this@AnimeDetailActivity)
                        .load(bitmapDrawable)
                        .diskCacheStrategy(
                            DiskCacheStrategy.AUTOMATIC
                        )
                        .skipMemoryCache(false)
                        .into(iv_anime_detail_activity_background)

                    //加载完背景图再显示
                    ObjectAnimator.ofFloat(iv_anime_detail_activity_background, "alpha", 0f, 1f)
                        .setDuration(250).start()
                }
            })
            iv_anime_detail_activity_cover.loadImage(it.cover)
            tv_anime_detail_activity_toolbar_title.text = it.title
            tv_anime_detail_activity_title.text = it.title
            tv_anime_detail_activity_alias.text = it.alias
            tv_anime_detail_activity_area.text = it.area
            tv_anime_detail_activity_year.text = it.year
            tv_anime_detail_activity_index.text =
                App.context.getString(R.string.anime_detail_index) + it.index
            tv_anime_detail_activity_type.visible()
            fl_anime_detail_activity_type.removeAllViews()
            for (i in it.animeType.indices) {
                val tvFlowLayout: TextView = layoutInflater
                    .inflate(
                        R.layout.item_anime_type_1,
                        fl_anime_detail_activity_type,
                        false
                    ) as TextView
                tvFlowLayout.text = it.animeType[i].title
                tvFlowLayout.setOnClickListener { it1 ->
                    //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                    process(
                        this,
                        Const.ActionUrl.ANIME_CLASSIFY +
                                "${it.animeType[i].actionUrl}类型/${it.animeType[i].title}"
                    )
                }
                fl_anime_detail_activity_type.addView(tvFlowLayout)
            }
            tv_anime_detail_activity_tag.visible()
            fl_anime_detail_activity_tag.removeAllViews()
            for (i in it.tag.indices) {
                val tvFlowLayout: TextView = layoutInflater
                    .inflate(
                        R.layout.item_anime_type_1,
                        fl_anime_detail_activity_tag,
                        false
                    ) as TextView
                tvFlowLayout.text = it.tag[i].title
                tvFlowLayout.setOnClickListener { it1 ->
                    //此处是”标签“，由于分类没有这一大项，因此传入”“串
                    process(
                        this,
                        Const.ActionUrl.ANIME_CLASSIFY +
                                "${it.tag[i].actionUrl}/${it.tag[i].title}"
                    )
                }
                fl_anime_detail_activity_tag.addView(tvFlowLayout)
            }
            tv_anime_detail_activity_info.text = it.info

            adapter.notifyDataSetChanged()
        })

        srl_anime_detail_activity.isRefreshing = true
        viewModel.getAnimeDetailData(partUrl)
    }
}