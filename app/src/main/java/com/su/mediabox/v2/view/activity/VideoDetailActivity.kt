package com.su.mediabox.v2.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.su.mediabox.R
import com.su.mediabox.config.Api
import com.su.mediabox.databinding.ActivityAnimeDetailBinding
import com.su.mediabox.util.Util.setTransparentStatusBar
import com.su.mediabox.util.showToast
import com.su.mediabox.view.fragment.ShareDialogFragment
import com.su.mediabox.util.coil.CoilUtil.loadGaussianBlurCover
import com.su.mediabox.v2.viewmodel.VideoDetailViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.activity.PlayActivity
import com.su.mediabox.view.adapter.type.initTypeList
import com.su.mediabox.view.adapter.type.linear
import com.su.mediabox.view.adapter.type.typeAdapter

class VideoDetailActivity : BasePluginActivity<ActivityAnimeDetailBinding>() {

    private val viewModel by viewModels<VideoDetailViewModel>()
    override var statusBarSkin: Boolean = false
    private var isClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransparentStatusBar(window, isDark = false)

        viewModel.partUrl = intent.getStringExtra("partUrl") ?: ""

        //详情数据列表
        mBinding.rvAnimeDetailActivityInfo.linear().initTypeList { }

        //下拉刷新
        mBinding.srlAnimeDetailActivity.setOnRefreshListener {

            viewModel.getAnimeDetailData()
        }

        mBinding.atbAnimeDetailActivityToolbar.run {
            setBackButtonClickListener { finish() }
            // 分享
            setButtonEnable(0, false)
            setButtonClickListener(0) {
                ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                    .show(supportFragmentManager, "share_dialog")
            }
            addButton(null)
            // 收藏
            setButtonEnable(1, false)
            setButtonClickListener(1) {
                isClick = true
                viewModel.switchFavState()
            }

            //收藏状态
            viewModel.isFavVideo.observe(this@VideoDetailActivity) {
                if (it) {
                    setButtonDrawable(1, R.drawable.ic_star_white_24_skin)
                    if (isClick)
                        getString(R.string.favorite_succeed).showToast()
                } else {
                    setButtonDrawable(1, R.drawable.ic_star_border_white_24)
                    if (isClick)
                        getString(R.string.remove_favorite_succeed).showToast()
                }
                isClick = false
            }

        }

        //详情数据
        viewModel.videoData.observe(this, Observer {
            mBinding.srlAnimeDetailActivity.isRefreshing = false
            mBinding.atbAnimeDetailActivityToolbar.apply {
                setButtonEnable(0, true)
                setButtonEnable(1, true)
            }

            if (viewModel.cover.isBlank()) return@Observer
            //高斯模糊封面背景
            mBinding.ivAnimeDetailActivityBackground.loadGaussianBlurCover(viewModel.cover, this)
            //标题
            mBinding.atbAnimeDetailActivityToolbar.titleText = viewModel.title
            //详情数据
            mBinding.rvAnimeDetailActivityInfo.typeAdapter().submitList(it.second)
        })

        mBinding.srlAnimeDetailActivity.isRefreshing = true
        viewModel.getAnimeDetailData()
    }

    override fun getBinding(): ActivityAnimeDetailBinding =
        ActivityAnimeDetailBinding.inflate(layoutInflater)

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        mBinding.rvAnimeDetailActivityInfo.typeAdapter().notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mBinding.rvAnimeDetailActivityInfo.typeAdapter().notifyDataSetChanged()
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        //主动向下一级路由目标提供一些信息
        intent?.apply {
            //封面
            putExtra(PlayActivity.INTENT_COVER, viewModel.cover)
            //详情链接
            putExtra(PlayActivity.INTENT_DPU, viewModel.partUrl)
        }
        super.startActivity(intent, options)
    }
}
