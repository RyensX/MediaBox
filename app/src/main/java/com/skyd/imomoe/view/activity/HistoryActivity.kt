package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.databinding.ActivityHistoryBinding
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.view.adapter.HistoryAdapter
import com.skyd.imomoe.viewmodel.HistoryViewModel

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        adapter = HistoryAdapter(this, viewModel.historyList)

        mBinding.run {
            atbHistoryActivity.setBackButtonClickListener { finish() }

            srlHistoryActivity.setColorSchemeColors(
                this@HistoryActivity.getResColor(R.color.main_color_skin)
            )
            srlHistoryActivity.setOnRefreshListener { viewModel.getHistoryList() }

            rvHistoryActivity.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rvHistoryActivity.adapter = adapter
        }


        viewModel.mldHistoryList.observe(this, Observer {
            adapter.notifyDataSetChanged()
            mBinding.srlHistoryActivity.isRefreshing = false
            if (it) {
                if (viewModel.historyList.isEmpty()) showLoadFailedTip(
                    getString(R.string.no_history),
                    null
                )
            }
        })

        viewModel.mldDeleteHistory.observe(this, Observer {
            if (viewModel.historyList.isEmpty()) showLoadFailedTip(
                getString(R.string.no_history),
                null
            )
            if (it >= 0) adapter.notifyItemRemoved(it)
        })

        viewModel.mldDeleteAllHistory.observe(this, Observer {
            showLoadFailedTip(getString(R.string.no_history), null)
            if (it > 0) adapter.notifyItemRangeRemoved(0, it)
        })

        mBinding.atbHistoryActivity.run {
            setButtonClickListener(0) {
                if (viewModel.historyList.isEmpty()) return@setButtonClickListener
                MaterialDialog(this@HistoryActivity).show {
                    icon(drawable = getResDrawable(R.drawable.ic_delete_main_color_2_24_skin))
                    title(res = R.string.warning)
                    message(text = "确定要删除所有观看历史记录？")
                    positiveButton(res = R.string.delete) { viewModel.deleteAllHistory() }
                    negativeButton(res = R.string.cancel) { dismiss() }
                }
            }
        }
    }

    override fun getBinding(): ActivityHistoryBinding =
        ActivityHistoryBinding.inflate(layoutInflater)

    override fun onResume() {
        super.onResume()

        mBinding.srlHistoryActivity.isRefreshing = true
        viewModel.getHistoryList()
    }

    fun deleteHistory(historyBean: HistoryBean) {
        viewModel.deleteHistory(historyBean)
    }

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutHistoryActivityNoHistory
}
