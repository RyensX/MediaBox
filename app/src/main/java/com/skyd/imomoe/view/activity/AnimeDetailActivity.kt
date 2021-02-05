package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.loadImage
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import kotlinx.android.synthetic.main.activity_anime_detail.*

class AnimeDetailActivity : BaseActivity() {
    private var partUrl: String = ""
    private lateinit var viewModel: AnimeDetailViewModel
    private lateinit var adapter: AnimeDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        viewModel = ViewModelProvider(this).get(AnimeDetailViewModel::class.java)
        adapter = AnimeDetailAdapter(this, viewModel.animeDetailBeanDataList)

        partUrl = intent.getStringExtra("partUrl") ?: ""

        val layoutManager = LinearLayoutManager(this)
        rv_anime_detail_activity_info.layoutManager = layoutManager
        rv_anime_detail_activity_info.setHasFixedSize(true)
        rv_anime_detail_activity_info.adapter = adapter

        srl_anime_detail_activity.setOnRefreshListener { viewModel.getAnimeDetailData(partUrl) }
        srl_anime_detail_activity.setColorSchemeResources(R.color.main_color)

        tv_anime_detail_activity_type.gone()
        tv_anime_detail_activity_tag.gone()

        viewModel.mldAnimeDetailData.observe(this, {
            if (srl_anime_detail_activity.isRefreshing)
                srl_anime_detail_activity.isRefreshing = false
            iv_anime_detail_activity_cover.loadImage(it.cover)
            tv_anime_detail_activity_title.text = it.title
            tv_anime_detail_activity_alias.text = it.alias
            tv_anime_detail_activity_area.text = it.area
            tv_anime_detail_activity_year.text = it.year
            tv_anime_detail_activity_index.text =
                App.context.getString(R.string.anime_detail_index) + it.index
            tv_anime_detail_activity_type.visible()
            fl_anime_detail_activity_type.removeAllViews()
            for (i in it.animeType.indices) {
                val linearLayout: LinearLayout = layoutInflater
                    .inflate(
                        R.layout.item_anime_type_1,
                        fl_anime_detail_activity_type,
                        false
                    ) as LinearLayout
                val tvFlowLayout =
                    linearLayout.findViewById<TextView>(R.id.tv_anime_type_1)
                tvFlowLayout.text = it.animeType[i]
                tvFlowLayout.setOnClickListener { it1 ->
                    process(this, "", it.animeType[i])
                }
                linearLayout.removeView(tvFlowLayout)
                fl_anime_detail_activity_type.addView(tvFlowLayout)
            }
            tv_anime_detail_activity_tag.visible()
            fl_anime_detail_activity_tag.removeAllViews()
            for (i in it.tag.indices) {
                val linearLayout: LinearLayout = layoutInflater
                    .inflate(
                        R.layout.item_anime_type_1,
                        fl_anime_detail_activity_tag,
                        false
                    ) as LinearLayout
                val tvFlowLayout =
                    linearLayout.findViewById<TextView>(R.id.tv_anime_type_1)
                tvFlowLayout.text = it.tag[i]
                tvFlowLayout.setOnClickListener { it1 ->
                    process(this, "", it.tag[i])
                }
                linearLayout.removeView(tvFlowLayout)
                fl_anime_detail_activity_tag.addView(tvFlowLayout)
            }
            tv_anime_detail_activity_info.text = it.info

            adapter.notifyDataSetChanged()
        })

        srl_anime_detail_activity.isRefreshing = true
        viewModel.getAnimeDetailData(partUrl)
    }
}