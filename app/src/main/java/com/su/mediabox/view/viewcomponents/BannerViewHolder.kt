package com.su.mediabox.view.viewcomponents

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.su.mediabox.R
import com.su.mediabox.databinding.ViewComponentBannerBinding
import com.su.mediabox.databinding.ViewComponentBannerItemBinding
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.been.BannerData
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.type.DataViewMapList
import com.su.mediabox.view.adapter.type.TypeAdapter
import com.su.mediabox.view.adapter.type.TypeViewHolder
import com.su.mediabox.view.adapter.type.registerDataViewMap
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

//TODO 无限轮播，由于需要改造adapter但使用了TypeAdapter因此暂不实现
//TODO 有时候因为加载大图片过慢导致列表重测和绑定
/**
 * 横幅视图组件
 */
class BannerViewHolder private constructor(private val binding: ViewComponentBannerBinding) :
    TypeViewHolder<BannerData>(binding.root) {

    private var mData: BannerData? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val autoPlayRunnable: Runnable = Runnable {
        nextBanner()
        onViewAttachedToWindow()
    }
    private var isPlaying = false

    private val vpAdapter = TypeAdapter(
        DataViewMapList().registerDataViewMap<BannerData.BannerItemData, BannerItemViewHolder>(),
        TypeAdapter.DefaultDiff
    )

    constructor(parent: ViewGroup) : this(
        ViewComponentBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.vcBannerView.apply {
            adapter = vpAdapter
        }
        binding.vcBannerIndicatorView.apply {
            setSliderColor(
                context.resources.getColor(R.color.banner_indicator_unselect),
                context.resources.getColor(R.color.banner_indicator_select)
            )
            setSliderWidth(12.dp.toFloat())
            setSliderHeight(3.dp.toFloat())
            setSlideMode(IndicatorSlideMode.WORM)
            setIndicatorStyle(IndicatorStyle.DASH)
            setupWithViewPager(binding.vcBannerView)
        }
        //监测窗体状态，没有显示时不轮播
        binding.root.addView(object : View(binding.root.context) {
            override fun onVisibilityChanged(changedView: View, visibility: Int) {
                super.onVisibilityChanged(changedView, visibility)
                if (visibility != VISIBLE)
                    onViewDetachedFromWindow()
                else if (!isPlaying)
                    onViewAttachedToWindow()
            }
        })
    }

    override fun onViewAttachedToWindow() {
        val interval = mData?.autoPlayInterval
        if (interval?.let { it > 0 } == true) {
            isPlaying = true
            mainHandler.removeCallbacks(autoPlayRunnable)
            mainHandler.postDelayed(autoPlayRunnable, interval)
        } else
            isPlaying = false
    }

    override fun onViewDetachedFromWindow() {
        isPlaying = false
        mainHandler.removeCallbacks(autoPlayRunnable)
    }

    private fun nextBanner() {
        binding.vcBannerView.apply {
            currentItem =
                if (currentItem < (this.adapter?.itemCount ?: 0) - 1) currentItem + 1 else 0
        }
    }

    override fun onBind(data: BannerData) {
        super.onBind(data)
        if (!isPlaying)
            onViewAttachedToWindow()
        mData = data
        binding.vcBannerViewCard.apply {
            if (radius.toInt() != data.round)
                radius = data.round.toFloat()
        }
        vpAdapter.submitList(data.bannerItems) {
            //TODO 未知原因无法显示
            binding.vcBannerIndicatorView.notifyDataChanged()
        }
    }

    class BannerItemViewHolder private constructor(private val binding: ViewComponentBannerItemBinding) :
        TypeViewHolder<BannerData.BannerItemData>(binding.root) {

        private var tmpData: BannerData.BannerItemData? = null

        constructor(parent: ViewGroup) : this(
            ViewComponentBannerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {
            setOnClickListener(binding.root) {
                tmpData?.action?.go(itemView.context)
            }
        }

        override fun onBind(data: BannerData.BannerItemData) {
            tmpData = data
            binding.apply {

                vcBannerItemImage.loadImage(data.imageUrl)

                vcBannerItemTitle.text = data.title
                vcBannerItemDesc.apply {
                    layoutParams.height = if (data.desc.isBlank()) 0 else WRAP_CONTENT
                }.text = data.desc
            }
        }
    }
}