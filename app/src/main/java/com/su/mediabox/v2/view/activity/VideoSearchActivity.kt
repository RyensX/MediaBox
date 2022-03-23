package com.su.mediabox.v2.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import com.su.mediabox.R
import com.su.mediabox.bean.SearchHistoryBean
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.databinding.ActivitySearchBinding
import com.su.mediabox.databinding.ItemSearchHistory1Binding
import com.su.mediabox.util.*
import com.su.mediabox.util.Util.showKeyboard
import com.su.mediabox.v2.viewmodel.VideoSearchViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.adapter.type.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoSearchActivity : BasePluginActivity<ActivitySearchBinding>() {

    companion object {
        const val EXTRA_KEY_WORK = "keyWord"

        private val searchDataViewMapList = DataViewMapList().apply {
            registerDataViewMap<SearchHistoryBean, SearchHistoryViewHolder>()
            addAll(TypeAdapter.globalDataViewMap)
        }
    }

    private val viewModel by viewModels<VideoSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mKeyWord = intent.getStringExtra(EXTRA_KEY_WORK) ?: ""

        mBinding.run {
            //列表
            rvSearchActivity
                .linear()
                .initTypeList(searchDataViewMapList) {
                    addViewHolderClickListener<SearchHistoryViewHolder> { pos ->
                        getData<SearchHistoryBean>(pos)?.also {
                            val keyWork = it.title.trim()
                            mBinding.etSearchActivitySearch.apply {
                                setText(keyWork)
                                setSelection(keyWork.length)
                            }
                            showLoading()
                            viewModel.getSearchData(keyWork)
                        }
                    }
                }
            //上拉刷新
            srlSearchActivity.setEnableRefresh(false)
            srlSearchActivity.setOnLoadMoreListener {
                showLoading()
                viewModel.getSearchData()
            }
            //搜索框
            mBinding.etSearchActivitySearch.apply {
                setOnEditorActionListener { _, action, _ ->
                    if (action == EditorInfo.IME_ACTION_SEARCH) {
                        isEnabled = false
                        viewModel.getSearchData(text.toString())
                    }
                    true
                }
            }
            //清空关键字
            etSearchActivitySearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ivSearchActivityClearKeyWords.apply {
                        if (s.isNullOrEmpty()) gone()
                        else visible()
                    }
                }
            })
            ivSearchActivityClearKeyWords.setOnClickListener {
                etSearchActivitySearch.setText("")
                viewModel.showSearchHistory()
            }
            //返回
            tvSearchActivityCancel.setOnClickListener { finish() }

            etSearchActivitySearch.showKeyboard()
        }

        viewModel.showState.observe(this) {
            mBinding.apply {
                srlSearchActivity.finishLoadMore()
                etSearchActivitySearch.isEnabled = true
                srlSearchActivity.setEnableLoadMore(false)
            }
            when (it) {
                VideoSearchViewModel.ShowState.KEYWORD -> {
                    mBinding.rvSearchActivity.typeAdapter().submitList(viewModel.resultData) {
                        mBinding.tvSearchActivityTip.gone()
                    }
                }
                VideoSearchViewModel.ShowState.RESULT -> {
                    mBinding.rvSearchActivity.typeAdapter().submitList(viewModel.resultData) {
                        val size = viewModel.resultData?.size ?: 0
                        if (size == 0)
                            getString(R.string.no_result_info).showToast()
                        else if (viewModel.lastLoadSize == 0)
                            getString(R.string.no_more_info).showToast()
                        else
                            mBinding.srlSearchActivity.setEnableLoadMore(true)
                        mBinding.tvSearchActivityTip.apply {
                            visible()
                            text = getString(R.string.search_activity_tip, viewModel.mKeyWord, size)
                        }
                        mBinding.layoutSearchActivityLoading.visibility = View.GONE
                    }
                }
                VideoSearchViewModel.ShowState.FAILED -> {
                    mBinding.rvSearchActivity.typeAdapter().submitList(null) {
                        mBinding.tvSearchActivityTip.gone()
                        mBinding.layoutSearchActivityLoading.visibility = View.GONE
                    }
                }
                else -> {}
            }
        }
    }

    override fun getBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }

    private fun showLoading() {
        mBinding.layoutSearchActivityLoading.apply {
            smartInflate()
            visibility = View.VISIBLE
        }
    }

    private class SearchHistoryViewHolder private constructor(private val binding: ItemSearchHistory1Binding) :
        TypeViewHolder<SearchHistoryBean>(binding.root) {

        companion object {
            private val coroutineScope by lazy(LazyThreadSafetyMode.NONE) {
                CoroutineScope(Dispatchers.IO)
            }
        }

        private var data: SearchHistoryBean? = null

        constructor(parent: ViewGroup) : this(
            ItemSearchHistory1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(binding.ivSearchHistory1Delete) {
                coroutineScope.launch {
                    data?.title?.also {
                        getAppDataBase().searchHistoryDao().deleteSearchHistory(it)
                    }
                }
            }
        }

        override fun onBind(data: SearchHistoryBean) {
            this.data = data
            binding.tvSearchHistory1Title.text = data.title
        }
    }
}
