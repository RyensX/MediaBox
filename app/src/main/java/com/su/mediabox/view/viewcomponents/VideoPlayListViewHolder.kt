package com.su.mediabox.view.viewcomponents

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.bean.HistoryBean
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ItemAnimeEpisode2Binding
import com.su.mediabox.databinding.ItemHorizontalRecyclerView1Binding
import com.su.mediabox.pluginapi.v2.been.EpisodeData
import com.su.mediabox.pluginapi.v2.been.VideoPlayListData
import com.su.mediabox.util.Util
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.PlayAction
import com.su.mediabox.util.Text.getEpisodeNum
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.bindHistoryPlayInfo
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.v2.view.activity.VideoMediaPlayActivity
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.episodeSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

/**
 * 播放列表视图组件
 */
class VideoPlayListViewHolder private constructor(private val binding: ItemHorizontalRecyclerView1Binding) :
    TypeViewHolder<VideoPlayListData>(binding.root) {

    var episodeDataList: List<EpisodeData>? = null
    private val coroutineScope = (binding.root.context as ComponentActivity).lifecycleScope
    private var lastEpisodeIndex: Int? = null
    private val isShowHistory = Pref.videoPlayListShowHistory

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
                bindingTypeAdapter.getTag<String>()?.also { name ->
                    episodeSheetDialog(binding.root.context, name, it)?.show()
                }
            }
        }
    }

    override fun onBind(data: VideoPlayListData) {
        super.onBind(data)
        coroutineScope.launch(Dispatchers.Default) {
            var list = data.playList
            runCatching {
                if (list.size > 1) {
                    //尝试自动排序为集数顺序
                    if (list[0].name.getEpisodeNum() > list[1].name.getEpisodeNum() || list[list.size - 2].name.getEpisodeNum() > list[list.size - 1].name.getEpisodeNum())
                        list = list.asReversed()
                }
            }
            episodeDataList = list

            withContext(Dispatchers.Main) {
                binding.rvHorizontalRecyclerView1.typeAdapter().apply {
                    setTag(list, Const.ViewComponent.EPISODE_LIST_TAG)
                    //自动定位
                    if (isShowHistory)
                        bindHistoryPlayInfo {
                            setTag(it, Const.ViewComponent.HISTORY_INFO_TAG)
                            submitList(episodeDataList) {
                                jumpEpisode(this)
                            }
                        }
                    else
                        submitList(episodeDataList)
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
            val target = adapter.getTag<HistoryBean>(Const.ViewComponent.HISTORY_INFO_TAG) ?: return
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

        constructor(parent: ViewGroup) : this(
            ItemAnimeEpisode2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) {

            setOnClickListener(itemView) { pos ->
                bindingTypeAdapter.getData<EpisodeData>(pos)?.also {
                    val action = it.action
                    if (action is PlayAction) {
                        //如果是跳转播放则填入播放列表
                        val playList =
                            bindingTypeAdapter.getTag<List<EpisodeData>>(Const.ViewComponent.EPISODE_LIST_TAG)
                        if (playList != null) {
                            VideoMediaPlayActivity.playList = playList
                        }
                    }
                    action?.go()
                }
            }

        }

        override fun onBind(data: EpisodeData) {
            val historyBean =
                bindingTypeAdapter.getTag<HistoryBean>(Const.ViewComponent.HISTORY_INFO_TAG)

            binding.tvAnimeEpisode2.apply {
                setTextColor(Color.WHITE)
            }.text = data.name

            binding.root.apply {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 6.dp, 8.dp, 6.dp)
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