package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityMonthAnimeBinding
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.MonthAnimeViewModel

class MonthAnimeActivity : BaseActivity<ActivityMonthAnimeBinding>() {
    private var partUrl: String = ""
    private lateinit var viewModel: MonthAnimeViewModel
    private lateinit var adapter: SearchAdapter
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        partUrl = intent.getStringExtra("partUrl") ?: ""

        viewModel = ViewModelProvider(this).get(MonthAnimeViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.monthAnimeList)

        val yearMonth = partUrl.replace("/", "")
        mBinding.run {
            llMonthAnimeActivityToolbar.tvToolbar1Title.text = getString(
                R.string.year_month_anime,
                yearMonth.substring(0, 4).toInt(),
                yearMonth.substring(4, 6).toInt()
            )

            rvMonthAnimeActivity.layoutManager = LinearLayoutManager(this@MonthAnimeActivity)
            rvMonthAnimeActivity.setHasFixedSize(true)
            rvMonthAnimeActivity.adapter = adapter

            llMonthAnimeActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }
            srlMonthAnimeActivity.setColorSchemeResources(R.color.main_color)
            srlMonthAnimeActivity.setOnRefreshListener { //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    viewModel.getMonthAnimeData(partUrl)
                } else {
                    srlMonthAnimeActivity.isRefreshing = false
                }
            }
        }

        viewModel.mldMonthAnimeList.observe(this, Observer {
            mBinding.srlMonthAnimeActivity.isRefreshing = false
            if (it) {
                hideLoadFailedTip()
            } else {
                showLoadFailedTip(
                    getString(R.string.load_data_failed_click_to_retry),
                    View.OnClickListener {
                        viewModel.getMonthAnimeData(partUrl)
                        hideLoadFailedTip()
                    })
            }
            adapter.notifyDataSetChanged()
        })

        mBinding.srlMonthAnimeActivity.isRefreshing = true
        viewModel.getMonthAnimeData(partUrl)
    }

    override fun getBinding(): ActivityMonthAnimeBinding =
        ActivityMonthAnimeBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutMonthAnimeActivityLoadFailed
}
