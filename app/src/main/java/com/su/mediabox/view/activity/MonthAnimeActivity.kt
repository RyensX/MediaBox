package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityMonthAnimeBinding
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.SearchAdapter
import com.su.mediabox.viewmodel.MonthAnimeViewModel

class MonthAnimeActivity : BasePluginActivity<ActivityMonthAnimeBinding>() {
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
