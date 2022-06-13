package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.su.mediabox.R
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.databinding.ActivityFavoriteBinding
import com.su.mediabox.databinding.ViewComponentFavBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.action.PlayAction
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.util.viewBind
import com.su.mediabox.viewmodel.MediaFavoriteViewModel
import com.su.mediabox.view.adapter.type.*

class MediaFavoriteActivity : BasePluginActivity() {

    private val mBinding by viewBind(ActivityFavoriteBinding::inflate)
    private val viewModel by viewModels<MediaFavoriteViewModel>()

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
                    DataViewMapList().registerDataViewMap<MediaFavorite, FavoriteViewHolder>(),
                    FavoriteDiff
                ) {}

            viewModel.favorite.observe(this@MediaFavoriteActivity) {
                rvFavoriteGrid.typeAdapter().submitList(it) {
                    if (it.isEmpty()) {
                        showLoadFailedTip(getString(R.string.no_favorite), null)
                    }
                }
            }
        }

    }

    override fun getLoadFailedTipView() = mBinding.layoutFavoriteActivityNoFavorite

    public class FavoriteViewHolder private constructor(private val binding: ViewComponentFavBinding) :
        TypeViewHolder<MediaFavorite>(binding.root) {

        private var data: MediaFavorite? = null

        constructor(parent: ViewGroup) : this(
            ViewComponentFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(binding.root) {
                data?.apply {
                    DetailAction.obtain(mediaUrl).go(itemView.context)
                }
            }
        }

        override fun onBind(data: MediaFavorite) {
            this.data = data
            binding.apply {
                vcFavCover.loadImage(data.cover)
                vcFavTitle.text = data.mediaTitle
                vcFavLastEpisode.text = data.lastEpisodeTitle?.let {
                    root.context.getString(R.string.already_seen_episode_x, it)
                } ?: root.context.getString(R.string.have_not_watched_this_anime)
            }
        }
    }

    object FavoriteDiff : DiffUtil.ItemCallback<MediaFavorite>() {
        override fun areItemsTheSame(
            oldItem: MediaFavorite,
            newItem: MediaFavorite
        ) = oldItem.mediaUrl == newItem.mediaUrl

        override fun areContentsTheSame(
            oldItem: MediaFavorite,
            newItem: MediaFavorite
        ) = oldItem.cover == newItem.cover &&
                oldItem.mediaTitle == newItem.mediaTitle &&
                oldItem.lastEpisodeTitle == newItem.lastEpisodeTitle &&
                oldItem.lastEpisodeUrl == newItem.lastEpisodeUrl
    }
}

