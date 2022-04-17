package com.su.mediabox.v2.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityMediaClassifyBinding
import com.su.mediabox.pluginapi.v2.action.ClassifyAction
import com.su.mediabox.util.Text
import com.su.mediabox.util.getAction
import com.su.mediabox.util.showToast
import com.su.mediabox.v2.viewmodel.MediaClassifyViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
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
            mediaClassifyList.dynamicGrid().initTypeList { }
            mediaClassifyFab.setOnClickListener {
                if (mediaClassify.data.isNullOrEmpty())
                    viewModel.getClassifyItemData()
                else
                    mediaClassify.show(supportFragmentManager)
            }
            mediaClassify.loadClassify = {
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
            mediaClassify.data = it
            mediaClassify.show(supportFragmentManager)
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