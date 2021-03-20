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
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.visible
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
            tbHistoryActivity.ivToolbar1Back.setOnClickListener { finish() }
            tbHistoryActivity.tvToolbar1Title.text = getString(R.string.watch_history)

            srlHistoryActivity.setColorSchemeColors(
                this@HistoryActivity.getResColor(R.color.main_color)
            )
            srlHistoryActivity.setOnRefreshListener { viewModel.getHistoryList() }

            rvHistoryActivity.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rvHistoryActivity.adapter = adapter
        }


        viewModel.mldHistoryList.observe(this, Observer {
            mBinding.srlHistoryActivity.isRefreshing = false
            if (it) {
                if (viewModel.historyList.isEmpty()) showLoadFailedTip(
                    getString(R.string.no_history),
                    null
                )
                adapter.notifyDataSetChanged()
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

        val padding = dp2px(12f)
        mBinding.tbHistoryActivity.run {
            ivToolbar1Button1.setPadding(padding, padding, padding, padding)
            ivToolbar1Button1.visible()
            ivToolbar1Button1.setImageResource(R.drawable.ic_delete_white_24)
            ivToolbar1Button1.setOnClickListener {
                if (viewModel.historyList.isEmpty()) return@setOnClickListener
                MaterialDialog(this@HistoryActivity).show {
                    icon(R.drawable.ic_delete_main_color_2_24)
                    title(text = "警告")
                    message(text = "确定要删除所有观看历史记录？")
                    positiveButton(text = "删除") { viewModel.deleteAllHistory() }
                    negativeButton(text = "取消") { dismiss() }
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
