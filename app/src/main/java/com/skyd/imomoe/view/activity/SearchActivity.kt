package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.showKeyboard
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.view.adapter.SearchHistoryAdapter
import com.skyd.imomoe.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_circle_progress_text_tip_1.*

class SearchActivity : BaseActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private lateinit var historyAdapter: SearchHistoryAdapter
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.searchResultList)
        historyAdapter = SearchHistoryAdapter(this, viewModel.searchHistoryList)

        val layoutManager = LinearLayoutManager(this)
        rv_search_activity.layoutManager = layoutManager
        rv_search_activity.setHasFixedSize(true)
        rv_search_activity.adapter = adapter

        et_search_activity_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                layout_circle_progress_text_tip_1?.gone()
                if (s == null || s.isEmpty()) {
                    tv_search_activity_tip.text = getString(R.string.search_history)
                    iv_search_activity_clear_key_words.gone()
                    viewModel.searchResultList.clear()
                    rv_search_activity.adapter = historyAdapter
                    historyAdapter.notifyDataSetChanged()
                } else iv_search_activity_clear_key_words.visible()
            }
        })

        iv_search_activity_clear_key_words.setOnClickListener {
            et_search_activity_search.setText("")
        }

        viewModel.mldFailed.observe(this, Observer {
            layout_circle_progress_text_tip_1.gone()
        })

        viewModel.mldSearchResultList.observe(this, Observer {
            layout_circle_progress_text_tip_1.gone()
            //仅在搜索框不为“”时展示搜索结果
            if (et_search_activity_search.text.toString().isNotEmpty()) {
                rv_search_activity.adapter = adapter
                cv_search_activity_tip.visible()
                tv_search_activity_tip.text = getString(
                    R.string.search_activity_tip, it,
                    viewModel.searchResultList.size
                )
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.mldSearchHistoryList.observe(this, Observer {
            if (viewModel.searchResultList.size == 0) {
                tv_search_activity_tip.text = getString(R.string.search_history)
                rv_search_activity.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
            }
        })

        viewModel.mldDeleteCompleted.observe(this, Observer {
            if (viewModel.searchResultList.size == 0) {
                rv_search_activity.adapter = historyAdapter
                historyAdapter.notifyItemRemoved(it)
            }
        })

        tv_search_activity_cancel.setOnClickListener { finish() }

        et_search_activity_search.showKeyboard()

        et_search_activity_search.setOnEditorActionListener(object :
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

    fun search(key: String) {
        //setText一定要在加载布局之前，否则progressbar会被gone掉
        et_search_activity_search.setText(key)
        et_search_activity_search.setSelection(key.length)
        if (layout_search_activity_loading == null) {
            layout_circle_progress_text_tip_1.visible()
        } else {
            layout_search_activity_loading.inflate()
        }
        viewModel.searchResultList.clear()
        tv_circle_progress_text_tip_1.gone()
        rv_search_activity.adapter = adapter
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