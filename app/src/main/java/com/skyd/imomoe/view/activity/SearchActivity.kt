package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.databinding.ActivitySearchBinding
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.showKeyboard
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.view.adapter.SearchHistoryAdapter
import com.skyd.imomoe.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private lateinit var mLayoutCircleProgressTextTip1: RelativeLayout
    private lateinit var tvCircleProgressTextTip1: TextView
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.searchResultList)
        historyAdapter = SearchHistoryAdapter(this, viewModel.searchHistoryList)

        mBinding.run {
            rvSearchActivity.layoutManager = LinearLayoutManager(this@SearchActivity)
            rvSearchActivity.setHasFixedSize(true)
            rvSearchActivity.adapter = adapter

            etSearchActivitySearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (this@SearchActivity::mLayoutCircleProgressTextTip1.isInitialized)
                        mLayoutCircleProgressTextTip1.gone()
                    if (s == null || s.isEmpty()) {
                        tvSearchActivityTip.text = getString(R.string.search_history)
                        ivSearchActivityClearKeyWords.gone()
                        viewModel.searchResultList.clear()
                        rvSearchActivity.adapter = historyAdapter
                        historyAdapter.notifyDataSetChanged()
                    } else ivSearchActivityClearKeyWords.visible()
                }
            })

            ivSearchActivityClearKeyWords.setOnClickListener {
                etSearchActivitySearch.setText("")
            }
        }

        viewModel.mldFailed.observe(this, Observer {
            if (this::mLayoutCircleProgressTextTip1.isInitialized) mLayoutCircleProgressTextTip1.gone()
        })

        viewModel.mldSearchResultList.observe(this, Observer {
            if (this::mLayoutCircleProgressTextTip1.isInitialized) mLayoutCircleProgressTextTip1.gone()
            //仅在搜索框不为“”时展示搜索结果
            if (mBinding.etSearchActivitySearch.text.toString().isNotEmpty()) {
                mBinding.rvSearchActivity.adapter = adapter
                mBinding.cvSearchActivityTip.visible()
                mBinding.tvSearchActivityTip.text = getString(
                    R.string.search_activity_tip, it,
                    viewModel.searchResultList.size
                )
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.mldSearchHistoryList.observe(this, Observer {
            if (viewModel.searchResultList.size == 0) {
                mBinding.tvSearchActivityTip.text = getString(R.string.search_history)
                mBinding.rvSearchActivity.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
            }
        })

        viewModel.mldDeleteCompleted.observe(this, Observer {
            if (viewModel.searchResultList.size == 0) {
                mBinding.rvSearchActivity.adapter = historyAdapter
                historyAdapter.notifyItemRemoved(it)
            }
        })

        mBinding.tvSearchActivityCancel.setOnClickListener { finish() }

        mBinding.etSearchActivitySearch.showKeyboard()

        mBinding.etSearchActivitySearch.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.text.toString().isBlank()) {
                        App.context.resources.getString(R.string.search_input_keywords_tips)
                            .showToast()
                        return false
                    }

                    //避免刷新间隔太短
                    return if (System.currentTimeMillis() - lastRefreshTime > 500) {
                        lastRefreshTime = System.currentTimeMillis()
                        search(v.text.toString())
                        true
                    } else {
                        false
                    }
                }
                return true
            }
        })

        viewModel.getSearchHistoryData()
    }

    override fun getBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    fun search(key: String) {
        //setText一定要在加载布局之前，否则progressbar会被gone掉
        mBinding.run {
            etSearchActivitySearch.setText(key)
            etSearchActivitySearch.setSelection(key.length)
            if (this@SearchActivity::tvCircleProgressTextTip1.isInitialized) {
                mLayoutCircleProgressTextTip1.visible()
            } else {
                mLayoutCircleProgressTextTip1 =
                    layoutSearchActivityLoading.inflate() as RelativeLayout
                tvCircleProgressTextTip1 =
                    mLayoutCircleProgressTextTip1.findViewById(R.id.tv_circle_progress_text_tip_1)
            }
            viewModel.searchResultList.clear()
            if (this@SearchActivity::tvCircleProgressTextTip1.isInitialized) tvCircleProgressTextTip1.gone()
            rvSearchActivity.adapter = adapter
        }
        viewModel.insertSearchHistory(
            SearchHistoryBean(
                "searchHistory1",
                "", System.currentTimeMillis(), key
            )
        )
        viewModel.getSearchData(key)
    }

    fun deleteSearchHistory(position: Int) {
        viewModel.deleteSearchHistory(position)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }
}
