package com.skyd.imomoe.view.activity

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivityAnimeDetailBinding
import com.skyd.imomoe.util.BlurUtils.blur
import com.skyd.imomoe.util.Util.getSkinResourceId
import com.skyd.imomoe.util.Util.getStatusBarHeight
import com.skyd.imomoe.util.Util.setTransparentStatusBar
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.glide.GlideUtil.getGlideUrl
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeDetailSpanSize
import com.skyd.imomoe.view.fragment.ShareDialogFragment
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import com.skyd.skin.core.listeners.ChangeSkinListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AnimeDetailActivity : BaseActivity<ActivityAnimeDetailBinding>() {
    private var partUrl: String = ""
    private var isFavorite: Boolean = false
    private lateinit var viewModel: AnimeDetailViewModel
    private lateinit var adapter: AnimeDetailAdapter
    override var statusBarSkin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransparentStatusBar(window, isDark = true)

        val statusBarLinearParams =
            mBinding.viewAnimeDetailActivityStatusBar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight()
        mBinding.viewAnimeDetailActivityStatusBar.layoutParams = statusBarLinearParams

        viewModel = ViewModelProvider(this).get(AnimeDetailViewModel::class.java)
        adapter = AnimeDetailAdapter(this, viewModel.animeDetailList)

        partUrl = intent.getStringExtra("partUrl") ?: ""

        mBinding.llAnimeDetailActivityToolbar.run {
            layoutToolbar1.setBackgroundColor(Color.TRANSPARENT)
            tvToolbar1Title.isFocused = true
            ivToolbar1Back.setOnClickListener { finish() }
            // 分享
            ivToolbar1Button1.visible()
            ivToolbar1Button1.setOnClickListener {
                ShareDialogFragment().setShareContent(Api.MAIN_URL + partUrl)
                    .show(supportFragmentManager, "share_dialog")
            }
            // 收藏
            GlobalScope.launch(Dispatchers.IO) {
                val favoriteAnime = getAppDataBase().favoriteAnimeDao().getFavoriteAnime(partUrl)
                isFavorite = if (favoriteAnime == null) {
                    ivToolbar1Button2.setImageResource(R.drawable.ic_star_border_white_24)
                    false
                } else {
                    ivToolbar1Button2.setImageResource(R.drawable.ic_star_white_24)
                    true
                }
                withContext(Dispatchers.Main) {
                    ivToolbar1Button2.visible()
                }
            }
            ivToolbar1Button2.isEnabled = false
            ivToolbar1Button2.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    if (isFavorite) {
                        getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(partUrl)
                        withContext(Dispatchers.Main) {
                            isFavorite = false
                            ivToolbar1Button2.setImageResource(R.drawable.ic_star_border_white_24)
                            getString(R.string.remove_favorite_succeed).showToast()
                        }
                    } else {
                        getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                            FavoriteAnimeBean(
                                Const.ViewHolderTypeString.ANIME_COVER_8, "",
                                partUrl,
                                viewModel.title,
                                System.currentTimeMillis(),
                                viewModel.cover
                            )
                        )
                        withContext(Dispatchers.Main) {
                            isFavorite = true
                            ivToolbar1Button2.setImageResource(R.drawable.ic_star_white_24)
                            getString(R.string.favorite_succeed).showToast()
                        }
                    }
                }
            }
        }

        mBinding.run {
            rvAnimeDetailActivityInfo.layoutManager = GridLayoutManager(this@AnimeDetailActivity, 4)
                .apply { spanSizeLookup = AnimeDetailSpanSize(adapter) }
            // 复用AnimeShow的ItemDecoration
            rvAnimeDetailActivityInfo.addItemDecoration(AnimeShowItemDecoration())
            rvAnimeDetailActivityInfo.adapter = adapter

            srlAnimeDetailActivity.setOnRefreshListener { viewModel.getAnimeDetailData(partUrl) }
            srlAnimeDetailActivity.setColorSchemeResources(getSkinResourceId(R.color.main_color_skin))
        }

        viewModel.mldAnimeDetailList.observe(this, Observer {
            mBinding.srlAnimeDetailActivity.isRefreshing = false
            if (!it) return@Observer
            mBinding.llAnimeDetailActivityToolbar.ivToolbar1Button2.isEnabled = true

            //先隐藏
            ObjectAnimator.ofFloat(mBinding.ivAnimeDetailActivityBackground, "alpha", 1f, 0f)
                .setDuration(250).start()
            if (viewModel.cover.url.isBlank()) return@Observer
            val glideUrl = getGlideUrl(viewModel.cover.url, viewModel.cover.referer)
            Glide.with(this).asBitmap().load(glideUrl).dontAnimate()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bitmapDrawable = BitmapDrawable(null, blur(resource))
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            bitmapDrawable.colorFilter = PorterDuffColorFilter(
                                Color.DKGRAY, PorterDuff.Mode.MULTIPLY
                            )
                        } else {
                            bitmapDrawable.colorFilter = PorterDuffColorFilter(
                                Color.LTGRAY, PorterDuff.Mode.MULTIPLY
                            )
                        }
                        mBinding.ivAnimeDetailActivityBackground.setImageDrawable(bitmapDrawable)

                        //加载完背景图再显示
                        ObjectAnimator.ofFloat(
                            mBinding.ivAnimeDetailActivityBackground,
                            "alpha",
                            0f,
                            1f
                        )
                            .setDuration(250).start()
                    }
                })
            mBinding.llAnimeDetailActivityToolbar.tvToolbar1Title.text = viewModel.title
            adapter.notifyDataSetChanged()
        })

        mBinding.srlAnimeDetailActivity.isRefreshing = true
        viewModel.getAnimeDetailData(partUrl)
    }

    override fun getBinding(): ActivityAnimeDetailBinding =
        ActivityAnimeDetailBinding.inflate(layoutInflater)

    fun getPartUrl(): String = partUrl

    override fun onChangeSkin() {
        super.onChangeSkin()
        mBinding.llAnimeDetailActivityToolbar.layoutToolbar1.setBackgroundColor(Color.TRANSPARENT)
        adapter.notifyDataSetChanged()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mBinding.llAnimeDetailActivityToolbar.layoutToolbar1.setBackgroundColor(Color.TRANSPARENT)
        adapter.notifyDataSetChanged()
    }
}
