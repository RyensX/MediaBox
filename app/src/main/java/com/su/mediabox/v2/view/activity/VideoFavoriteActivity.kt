package com.su.mediabox.v2.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.su.mediabox.R
import com.su.mediabox.bean.FavoriteAnimeBean
import com.su.mediabox.databinding.ActivityFavoriteBinding
import com.su.mediabox.databinding.ViewComponentFavBinding
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.action.PlayAction
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.v2.viewmodel.VideoFavoriteViewModel
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.view.adapter.type.*

class VideoFavoriteActivity : BasePluginActivity<ActivityFavoriteBinding>() {

    private val viewModel by viewModels<VideoFavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            atbFavoriteActivity.setBackButtonClickListener { finish() }

            rvFavoriteGrid
                .grid(3)
                .apply {
                    addItemDecoration(DynamicGridItemDecoration(12.dp))
                }
                .initTypeList(
                    DataViewMapList().registerDataViewMap<FavoriteAnimeBean, FavoriteViewHolder>(),
                    FavoriteDiff
                ) {}

            viewModel.favorite.observe(this@VideoFavoriteActivity) {
                rvFavoriteGrid.typeAdapter().submitList(it) {
                    if (it.isEmpty()) {
                        showLoadFailedTip(getString(R.string.no_favorite), null)
                    }
                }
            }
        }

    }

    override fun getBinding(): ActivityFavoriteBinding =
        ActivityFavoriteBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView() = mBinding.layoutFavoriteActivityNoFavorite

    class FavoriteViewHolder private constructor(private val binding: ViewComponentFavBinding) :
        TypeViewHolder<FavoriteAnimeBean>(binding.root) {

        private var data: FavoriteAnimeBean? = null

        constructor(parent: ViewGroup) : this(
            ViewComponentFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(binding.root) {
                data?.apply {
                    //有播放记录时点击直接续播，没有则打开介绍
                    if (lastEpisodeUrl != null)
                        PlayAction.obtain(lastEpisodeUrl!!, cover, animeUrl, animeTitle)
                            .go(itemView.context)
                    else
                        DetailAction.obtain(animeUrl).go(itemView.context)
                }
            }
            setOnLongClickListener(binding.root) {
                data?.animeUrl?.also {
                    DetailAction.obtain(it).go(itemView.context)
                }
                true
            }
        }

        override fun onBind(data: FavoriteAnimeBean) {
            this.data = data
            binding.apply {
                vcFavCover.loadImage(data.cover)
                vcFavTitle.text = data.animeTitle
                vcFavLastEpisode.text = data.lastEpisode?.let {
                    root.context.getString(R.string.already_seen_episode_x, it)
                } ?: root.context.getString(R.string.have_not_watched_this_anime)
            }
        }
    }

    object FavoriteDiff : DiffUtil.ItemCallback<FavoriteAnimeBean>() {
        override fun areItemsTheSame(
            oldItem: FavoriteAnimeBean,
            newItem: FavoriteAnimeBean
        ) = oldItem.animeUrl == newItem.animeUrl

        override fun areContentsTheSame(
            oldItem: FavoriteAnimeBean,
            newItem: FavoriteAnimeBean
        ) = oldItem.cover == newItem.cover &&
                oldItem.animeTitle == newItem.animeTitle &&
                oldItem.lastEpisode == newItem.lastEpisode &&
                oldItem.lastEpisodeUrl == newItem.lastEpisodeUrl
    }
}

