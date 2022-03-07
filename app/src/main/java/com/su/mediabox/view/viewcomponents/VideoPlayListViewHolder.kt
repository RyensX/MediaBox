package com.su.mediabox.view.viewcomponents

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.R
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.databinding.ItemAnimeEpisode2Binding
import com.su.mediabox.databinding.ItemHorizontalRecyclerView1Binding
import com.su.mediabox.plugin.AppRouteProcessor
import com.su.mediabox.pluginapi.v2.been.EpisodeData
import com.su.mediabox.pluginapi.v2.been.VideoPlayListData
import com.su.mediabox.util.Util
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.bindHistoryPlayInfo
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.episodeSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * 播放列表视图组件
 */
class VideoPlayListViewHolder private constructor(private val binding: ItemHorizontalRecyclerView1Binding) :
    TypeViewHolder<VideoPlayListData>(binding.root) {

    var episodeDataList: List<EpisodeData>? = null
    private val coroutineScope = (binding.root.context as ComponentActivity).lifecycleScope
    private var lastEpisodeIndex: Int? = null

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

        binding.rvHorizontalRecyclerView1.typeAdapter().apply {
            //TODO 支持定义不绑定历史播放信息
            bindHistoryPlayInfo {
                setTag(it)
                submitList(data.playList) {
                    jumpEpisode(this)
                }
            }
        }

        binding.ivHorizontalRecyclerView1More.apply {
            setImageDrawable(Util.getResDrawable(R.drawable.ic_keyboard_arrow_down_main_color_2_24_skin))
            imageTintList =
                ColorStateList.valueOf(binding.root.context.getResColor(R.color.foreground_white_skin))
        }
    }

    /**
     * 自动跳转到历史剧集位置
     */
    private fun jumpEpisode(adapter: TypeAdapter) {
        binding.rvHorizontalRecyclerView1.apply {
            val target = adapter.getTag<HistoryBean>() ?: return
            coroutineScope.launch {
                episodeDataList?.forEachIndexed { index, data ->
                    if (data.url == target.lastEpisodeUrl) {
                        val jumpLength = (index - (lastEpisodeIndex ?: 0)).absoluteValue
                        launch(Dispatchers.Main) {
                            //更新新位置
                            adapter.notifyItemChanged(index)
                            lastEpisodeIndex?.also {
                                //如果有上一个位置页更新
                                adapter.notifyItemChanged(it)
                            }
                            if (jumpLength <= 30)
                                smoothScrollToPosition(index)
                            else
                            //过长直接跳转
                                scrollToPosition(index)

                            lastEpisodeIndex = index
                        }

                        return@forEachIndexed
                    }
                }
            }
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

            setOnClickListener(itemView) { pos ->
                adapter?.getData<EpisodeData>(pos)?.also {
                    AppRouteProcessor.process(it.actionUrl)
                }
            }

        }

        override fun onBind(data: EpisodeData) {
            val historyBean = adapter?.getTag<HistoryBean>()

            binding.tvAnimeEpisode2.apply {
                setTextColor(Color.WHITE)
            }.text = data.name

            binding.root.apply {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 4.dp, 8.dp, 4.dp)
                }

                if (data.url == historyBean?.lastEpisodeUrl) {
                    //有对应播放记录，高亮显示并置顶
                    setTextColor(binding.root.context.getResColor(R.color.foreground_main_color_2_skin))
                } else {
                    setTextColor(binding.root.context.getResColor(R.color.foreground_white_skin))
                }

                //setTextColor(binding.root.context.getResColor(R.color.foreground_white_skin))
                background =
                    Util.getResDrawable(R.drawable.shape_circle_corner_edge_white_ripper_5_skin)
            }
        }
    }
}