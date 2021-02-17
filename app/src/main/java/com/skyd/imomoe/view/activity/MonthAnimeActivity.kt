package com.skyd.imomoe.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.MonthAnimeViewModel
import com.skyd.imomoe.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_month_anime.*
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*

class MonthAnimeActivity : AppCompatActivity() {
    private var partUrl: String = ""
    private lateinit var viewModel: MonthAnimeViewModel
    private lateinit var adapter: SearchAdapter
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_anime)

        partUrl = intent.getStringExtra("partUrl") ?: ""

        viewModel = ViewModelProvider(this).get(MonthAnimeViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.monthAnimeList)

        val yearMonth = partUrl.replace("/", "")
        tv_toolbar_1_title.text = getString(
            R.string.year_month_anime,
            yearMonth.substring(0, 4).toInt(),
            yearMonth.substring(4, 6).toInt()
        )

        val layoutManager = LinearLayoutManager(this)
        rv_month_anime_activity.layoutManager = layoutManager
        rv_month_anime_activity.setHasFixedSize(true)
        rv_month_anime_activity.adapter = adapter

        iv_toolbar_1_back.setOnClickListener { finish() }
        srl_month_anime_activity.setColorSchemeResources(R.color.main_color)
        srl_month_anime_activity.setOnRefreshListener { //避免刷新间隔太短
            if (System.currentTimeMillis() - lastRefreshTime > 500) {
                lastRefreshTime = System.currentTimeMillis()
                viewModel.getMonthAnimeData(partUrl)
            } else {
                srl_rank_activity.isRefreshing = false
            }
        }

        viewModel.mldMonthAnimeList.observe(this, {
            srl_month_anime_activity.isRefreshing = false
            adapter.notifyDataSetChanged()
        })

        srl_month_anime_activity.isRefreshing = true
        viewModel.getMonthAnimeData(partUrl)
    }
}