package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivitySearchBinding
import com.skyd.imomoe.util.Util.showKeyboard
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.smartNotifyDataSetChanged
import com.skyd.imomoe.util.visible
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

        val pageNumber = intent.getStringExtra("pageNumber") ?: ""
        viewModel.keyWord = intent.getStringExtra("keyWord") ?: ""

        mBinding.run {
            srlSearchActivity.setEnableRefresh(false)
            srlSearchActivity.setOnLoadMoreListener {
                viewModel.pageNumberBean?.let {
                    viewModel.getSearchData(viewModel.keyWord, isRefresh = false, it.actionUrl)
                    return@setOnLoadMoreListener
                }
                mBinding.srlSearchActivity.finishLoadMore()
                getString(R.string.no_more_info).showToast()
            }

            rvSearchActivity.layoutManager = LinearLayoutManager(this@SearchActivity)
            rvSearchActivity.setHasFixedSize(true)
            setSearchAdapter()

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
                        setHistoryAdapter()
                        historyAdapter.notifyDataSetChanged()
                    } else ivSearchActivityClearKeyWords.visible()
                }
            })

            ivSearchActivityClearKeyWords.setOnClickListener {
                etSearchActivitySearch.setText("")
            }
        }

        viewModel.mldSearchResultList.observe(this, {
            mBinding.srlSearchActivity.closeHeaderOrFooter()
            if (this::mLayoutCircleProgressTextTip1.isInitialized) mLayoutCircleProgressTextTip1.gone()
            // 仅在搜索框不为“”时展示搜索结果
            if (mBinding.etSearchActivitySearch.text.toString().isNotEmpty()) {
                if (mBinding.rvSearchActivity.adapter != adapter) setSearchAdapter()
                adapter.smartNotifyDataSetChanged(it.first, it.second, viewModel.searchResultList)
                when (it.first) {
                    ResponseDataType.REFRESH, ResponseDataType.LOAD_MORE -> {
                        mBinding.tvSearchActivityTip.text = getString(
                            R.string.search_activity_tip, viewModel.keyWord,
                            viewModel.searchResultList.size
                        )
                    }
                    ResponseDataType.FAILED -> {
                        mBinding.tvSearchActivityTip.text =
                            getString(R.string.search_activity_failed)
                    }
                }
            }
        })

        viewModel.mldSearchHistoryList.observe(this, {
            if (viewModel.searchResultList.size == 0) {
                mBinding.tvSearchActivityTip.text = getString(R.string.search_history)
                setHistoryAdapter()
                historyAdapter.notifyDataSetChanged()
            }
        })

        viewModel.mldDeleteCompleted.observe(this, {
            if (viewModel.searchResultList.size == 0) {
                setHistoryAdapter()
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

        if (viewModel.keyWord.isBlank()) viewModel.getSearchHistoryData()
        else search(viewModel.keyWord, pageNumber)
    }

    override fun getBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    fun search(key: String, partUrl: String = "") {
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
            setSearchAdapter()
        }
        viewModel.insertSearchHistory(
            SearchHistoryBean(
                Const.ViewHolderTypeString.SEARCH_HISTORY_1,
                "", System.currentTimeMillis(), key
            )
        )
        viewModel.getSearchData(key, isRefresh = true, partUrl = partUrl)
    }

    fun deleteSearchHistory(position: Int) {
        viewModel.deleteSearchHistory(position)
    }

    private fun setSearchAdapter() {
        mBinding.rvSearchActivity.adapter = adapter
        mBinding.srlSearchActivity.setEnableLoadMore(true)
    }

    private fun setHistoryAdapter() {
        mBinding.rvSearchActivity.adapter = historyAdapter
        mBinding.srlSearchActivity.setEnableLoadMore(false)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }
}
