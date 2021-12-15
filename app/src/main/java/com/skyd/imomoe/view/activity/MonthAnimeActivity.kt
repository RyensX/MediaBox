package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityMonthAnimeBinding
import com.skyd.imomoe.util.showToast
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

        mBinding.run {
            atbMonthAnimeActivity.titleText = getString(R.string.year_month_anime, partUrl)

            rvMonthAnimeActivity.layoutManager = LinearLayoutManager(this@MonthAnimeActivity)
            rvMonthAnimeActivity.setHasFixedSize(true)
            rvMonthAnimeActivity.adapter = adapter

            atbMonthAnimeActivity.setBackButtonClickListener { finish() }
            srlMonthAnimeActivity.setOnRefreshListener { //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    viewModel.getMonthAnimeData(partUrl)
                } else {
                    srlMonthAnimeActivity.closeHeaderOrFooter()
                }
            }
            srlMonthAnimeActivity.setOnLoadMoreListener {
                viewModel.pageNumberBean?.let {
                    viewModel.getMonthAnimeData(it.actionUrl, isRefresh = false)
                    return@setOnLoadMoreListener
                }
                mBinding.srlMonthAnimeActivity.finishLoadMore()
                getString(R.string.no_more_info).showToast()
            }
        }

        viewModel.mldMonthAnimeList.observe(this, Observer {
            mBinding.srlMonthAnimeActivity.closeHeaderOrFooter()
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

        mBinding.srlMonthAnimeActivity.autoRefresh()
    }

    override fun getBinding(): ActivityMonthAnimeBinding =
        ActivityMonthAnimeBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutMonthAnimeActivityLoadFailed
}
