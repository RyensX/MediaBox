package com.su.mediabox.view.viewcomponents

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.databinding.ItemAnimeEpisode2Binding
import com.su.mediabox.databinding.ItemHorizontalRecyclerView1Binding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.EpisodeData
import com.su.mediabox.pluginapi.v2.been.VideoPlayListData
import com.su.mediabox.util.Util
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.episodeSheetDialog

/**
 * 播放列表视图组件
 */
class VideoPlayListViewHolder private constructor(private val binding: ItemHorizontalRecyclerView1Binding) :
    TypeViewHolder<VideoPlayListData>(binding.root) {

    var episodeDataList: List<EpisodeData>? = null

    constructor(parent: ViewGroup) : this(
        ItemHorizontalRecyclerView1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) {
        binding.rvHorizontalRecyclerView1
            .linear(RecyclerView.HORIZONTAL)
            .initTypeList { }

        binding.ivHorizontalRecyclerView1More.setOnClickListener {
            episodeDataList?.also {
                episodeSheetDialog(binding.root.context, it)?.show()
            }
        }
    }

    override fun onBind(data: VideoPlayListData) {
        episodeDataList = data.playList

        binding.rvHorizontalRecyclerView1.typeAdapter().submitList(data.playList)
        binding.ivHorizontalRecyclerView1More.apply {
            setImageDrawable(Util.getResDrawable(R.drawable.ic_keyboard_arrow_down_main_color_2_24_skin))
            imageTintList =
                ColorStateList.valueOf(binding.root.context.getResColor(R.color.foreground_white_skin))
        }
    }

    //剧集视图
    open class EpisodeViewHolder(protected val binding: ItemAnimeEpisode2Binding) :
        TypeViewHolder<EpisodeData>(binding.root) {

        private var adapter: TypeAdapter? = null

        constructor(parent: ViewGroup) : this(
            ItemAnimeEpisode2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {
            adapter = (parent as? RecyclerView)?.typeAdapter()
        }

        init {
            setOnClickListener(itemView) { pos ->
                adapter?.getData<EpisodeData>(pos)?.also {
                    AppRouteProcessor.process(it.actionUrl)
                }
            }
        }

        override fun onBind(data: EpisodeData) {
            binding.tvAnimeEpisode2.apply {
                setTextColor(Color.WHITE)
            }.text = data.name

            binding.root.apply {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 4.dp, 8.dp, 4.dp)
                }
                setTextColor(binding.root.context.getResColor(R.color.foreground_white_skin))
                background =
                    Util.getResDrawable(R.drawable.shape_circle_corner_edge_white_ripper_5_skin)
            }
        }
    }
}