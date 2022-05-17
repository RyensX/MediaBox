package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityMediaClassifyBinding
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.util.*
import com.su.mediabox.viewmodel.MediaClassifyViewModel
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.typeAdapter
import com.su.mediabox.view.fragment.MediaClassifyBottomSheetDialogFragment

class MediaClassifyActivity : BasePluginActivity<ActivityMediaClassifyBinding>() {

    private val viewModel by viewModels<MediaClassifyViewModel>()
    private val mediaClassify = MediaClassifyBottomSheetDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            mediaClassifyToolbar.apply {
                titleText = getString(R.string.classify_title)
                setBackButtonClickListener { finish() }
            }
            mediaClassifyList.dynamicGrid().initTypeList {
                mediaClassifyList.isNestedScrollingEnabled = true
            }

            mediaClassifyFab.setOnClickListener {
                if (mediaClassify.data.isNullOrEmpty())
                    viewModel.getClassifyItemData()
                else
                    mediaClassify.show(supportFragmentManager)
            }
            mediaClassify.loadClassify = {
                mediaClassifyList.smoothScrollToPosition(0)
                viewModel.getClassifyData(it)
            }

            mediaClassifySwipe.apply {
                setEnableRefresh(false)
                //上拉加载更新
                setOnLoadMoreListener {
                    viewModel.getClassifyData()
                }
            }
        }

        //当前分类
        viewModel.currentClassify.observe(this) {
            //更新标题
            mBinding.mediaClassifyToolbar.titleText =
                Text.formatMergedStr(" - ", it.classifyCategory, it.classify)
            //更新分类弹窗当前分类
            mediaClassify.currentClassifyAction = it
        }

        //分类项数据
        viewModel.classifyItemDataList.observe(this) {
            when (it) {
                is DataState.INIT -> {
                    mBinding.mediaClassifyFabProgress.invisible()
                }
                is DataState.LOADING -> {
                    mBinding.apply {
                        mediaClassifyFab.disable()
                        mediaClassifyFabProgress.visible()
                    }
                }
                is DataState.SUCCESS<*> -> {
                    mBinding.apply {
                        mediaClassifyFab.enable()
                        mediaClassifyFabProgress.hide()
                    }
                    mediaClassify.data = it.getData()
                    mediaClassify.show(supportFragmentManager)
                }
                is DataState.FAILED -> {
                    mBinding.apply {
                        mediaClassifyFab.enable()
                        mediaClassifyFabProgress.invisible()
                    }
                    "加载分类项错误：${it.throwable?.message}".showToast()
                }
            }
        }

        //分类数据
        viewModel.classifyDataList.observe(this) {
            mBinding.mediaClassifySwipe.finishLoadMore()
            mBinding.mediaClassifyList.typeAdapter().submitList(it) {
                if (mediaClassify.dialog?.isShowing == true)
                    mediaClassify.dismiss()
                if (it.isEmpty()) {
                    getString(R.string.no_more_info).showToast()
                }
            }
        }

        //如果传入分类，则直接开始加载分类数据，否则自动加载分类项数据并打开弹窗
        getAction<ClassifyAction>()?.also {
            mediaClassify.currentClassifyAction = it
            viewModel.getClassifyData(it)
        } ?: viewModel.getClassifyItemData()
    }

    override fun getBinding() = ActivityMediaClassifyBinding.inflate(layoutInflater)

}