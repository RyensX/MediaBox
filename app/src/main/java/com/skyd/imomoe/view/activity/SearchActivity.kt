package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.showKeyboard
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import com.skyd.imomoe.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.searchResultList)

        val layoutManager = LinearLayoutManager(this)
        rv_search_activity.layoutManager = layoutManager
        rv_search_activity.setHasFixedSize(true)
        rv_search_activity.adapter = adapter

        viewModel.mldSearchResultList.observe(this, {
            tv_search_activity_tip.text = App.context.getString(
                R.string.search_activity_tip, it,
                viewModel.searchResultList.size
            )
            adapter.notifyDataSetChanged()
        })

        tv_search_activity_cancel.setOnClickListener {
            finish()
        }

        et_search_activity_search.showKeyboard()

        et_search_activity_search.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.text.toString().isEmpty()) {
                        App.context.resources.getString(R.string.search_input_keywords_tips)
                            .showToast()
                        return false
                    }
                    viewModel.getSearchData(et_search_activity_search.text.toString())
                    return true
                }
                return true
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }
}