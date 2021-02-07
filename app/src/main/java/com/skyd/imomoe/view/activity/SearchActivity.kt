package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.showKeyboard
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        adapter = SearchAdapter(this, viewModel.searchResultList)

        cv_search_activity_tip.gone()

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
                if (s == null || s.isEmpty()) iv_search_activity_clear_key_words.gone()
                else iv_search_activity_clear_key_words.visible()
            }
        })

        iv_search_activity_clear_key_words.setOnClickListener {
            et_search_activity_search.setText("")
        }

        viewModel.mldFailed.observe(this, {
            pb_search_activity_loading.gone()
        })

        viewModel.mldSearchResultList.observe(this, {
            pb_search_activity_loading.gone()
            cv_search_activity_tip.visible()
            tv_search_activity_tip.text = App.context.getString(
                R.string.search_activity_tip, it,
                viewModel.searchResultList.size
            )
            adapter.notifyDataSetChanged()
        })

        tv_search_activity_cancel.setOnClickListener { finish() }

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

                    //避免刷新间隔太短
                    return if (System.currentTimeMillis() - lastRefreshTime > 500) {
                        lastRefreshTime = System.currentTimeMillis()
                        pb_search_activity_loading.visible()
                        viewModel.getSearchData(et_search_activity_search.text.toString())
                        true
                    } else {
                        false
                    }
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