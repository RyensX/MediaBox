package com.su.mediabox.view.fragment.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.bean.DefaultEmpty
import com.su.mediabox.bean.MediaFavorite
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.databinding.FragmentMediaFavoriteBinding
import com.su.mediabox.databinding.ViewComponentFavBinding
import com.su.mediabox.lifecycleCollect
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.appCoroutineScope
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.viewmodel.MediaDataViewModel
import kotlinx.coroutines.launch

class MediaFavoriteDataPageFragment : BaseFragment() {

    private lateinit var mBinding: FragmentMediaFavoriteBinding
    private val viewModel by activityViewModels<MediaDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMediaFavoriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun pagerInit() {
        mBinding.dataList
            .grid(3)
            .apply {
                addItemDecoration(DynamicGridItemDecoration(10.dp, hasTopBottomEdge = false))
                (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int) =
                            if (typeAdapter().getItem(position) is DefaultEmpty) 3 else 1
                    }
            }
            .initTypeList(
                DataViewMapList().registerDataViewMap<MediaFavorite, FavoriteViewHolder>(),
                FavoriteDiff
            ) {
                vHCreateDSL<FavoriteViewHolder> {
                    setOnLongClickListener(itemView) {
                        getData<MediaFavorite>(it)?.let { data ->
                            PopupMenu(bindingContext, itemView).apply {
                                menu.add(R.string.delete)
                                setOnMenuItemClickListener {
                                    appCoroutineScope.launch {
                                        getAppDataBase().favoriteDao()
                                            .deleteFavoritesAndUpdateRecords(data.mediaUrl)
                                    }
                                    true
                                }
                                show()
                            }
                        }
                        true
                    }
                }
                lifecycleCollect(viewModel.favorite) {
                    if (it != null)
                        submitList(it) {
                            mBinding.dataFilterResult.text =
                                getString(R.string.media_data_page_filter_result_format, it.size)
                            if (viewModel.filterCount > 0)
                                mBinding.dataList.smoothScrollToPosition(0)
                        }
                }
            }
        mBinding.dataFilter.addTextChangedListener {
            viewModel.filter(it?.toString())
        }
    }

    class FavoriteViewHolder private constructor(private val binding: ViewComponentFavBinding) :
        TypeViewHolder<MediaFavorite>(binding.root) {

        private var data: MediaFavorite? = null

        constructor(parent: ViewGroup) : this(
            ViewComponentFavBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(binding.root) {
                data?.apply {
                    DetailAction.obtain(mediaUrl).go(bindingContext)
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

    object FavoriteDiff : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(
            oldItem: Any,
            newItem: Any
        ) =
            oldItem is MediaFavorite && newItem is MediaFavorite && oldItem.mediaUrl == newItem.mediaUrl

        override fun areContentsTheSame(
            oldItem: Any,
            newItem: Any
        ) =
            oldItem is MediaFavorite && newItem is MediaFavorite && oldItem.cover == newItem.cover &&
                    oldItem.mediaTitle == newItem.mediaTitle &&
                    oldItem.lastEpisodeTitle == newItem.lastEpisodeTitle &&
                    oldItem.lastEpisodeUrl == newItem.lastEpisodeUrl
    }

}