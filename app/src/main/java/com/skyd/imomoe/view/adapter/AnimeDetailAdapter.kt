package com.skyd.imomoe.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.IAnimeDetailBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.activity.AnimeDetailActivity
import com.skyd.imomoe.view.adapter.decoration.AnimeCoverItemDecoration
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.component.BottomSheetRecyclerView

class AnimeDetailAdapter(
    val activity: AnimeDetailActivity,
    private val dataList: List<IAnimeDetailBean>
) : BaseRvAdapter(dataList) {

    private val gridItemDecoration = AnimeCoverItemDecoration()

    private val animeEpisodeItemDecoration = AnimeEpisodeItemDecoration()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when {
            holder is Header1ViewHolder -> {
                holder.tvHeader1Title.textSize = 15f
                holder.tvHeader1Title.text = item.title
                holder.tvHeader1Title.setTextColor(
                    activity.getResColor(R.color.foreground_white_skin)
                )
            }
            holder is GridRecyclerView1ViewHolder -> {
                item.animeCoverList?.let {
                    val layoutManager = GridLayoutManager(activity, 4)
                    holder.rvGridRecyclerView1.post {
                        holder.rvGridRecyclerView1.setPadding(16.dp, 0, 16.dp, 0)
                    }
                    if (holder.rvGridRecyclerView1.itemDecorationCount == 0) {
                        holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                    }
                    holder.rvGridRecyclerView1.layoutManager = layoutManager
                    holder.rvGridRecyclerView1.adapter =
                        AnimeShowAdapter.GridRecyclerView1Adapter(
                            activity, it,
                            activity.getResColor(R.color.foreground_white_skin)
                        )
                }
            }
            holder is HorizontalRecyclerView1ViewHolder -> {
                item.episodeList?.let {
                    holder.rvHorizontalRecyclerView1.adapter.let { adapter ->
                        if (adapter == null) {
                            holder.rvHorizontalRecyclerView1.adapter =
                                EpisodeRecyclerView1Adapter(
                                    activity, it, detailPartUrl = activity.getPartUrl()
                                )
                        } else adapter.notifyDataSetChanged()
                    }
                    holder.ivHorizontalRecyclerView1More.setImageDrawable(getResDrawable(R.drawable.ic_keyboard_arrow_down_main_color_2_24_skin))
                    holder.ivHorizontalRecyclerView1More.imageTintList =
                        ColorStateList.valueOf(activity.getResColor(R.color.foreground_white_skin))
                    holder.ivHorizontalRecyclerView1More.setOnClickListener { it1 ->
                        showEpisodeSheetDialog(it).show()
                    }
                }
            }
            holder is AnimeDescribe1ViewHolder -> {
                holder.tvAnimeDescribe1.text = item.describe
                holder.tvAnimeDescribe1.setOnClickListener { }
                holder.tvAnimeDescribe1.setTextColor(
                    activity.getResColor(R.color.foreground_white_skin)
                )
            }
            holder is AnimeInfo1ViewHolder -> {
                item.headerInfo?.let {
                    holder.ivAnimeInfo1Cover.setTag(R.id.image_view_tag, it.cover.url)
                    if (holder.ivAnimeInfo1Cover.getTag(R.id.image_view_tag) == it.cover.url) {
                        holder.ivAnimeInfo1Cover.loadImage(
                            it.cover.url,
                            referer = it.cover.referer,
                            placeholder = 0,
                            error = 0
                        )
                    }
                    holder.tvAnimeInfo1Title.text = it.title
                    holder.tvAnimeInfo1Alias.text = it.alias
                    holder.tvAnimeInfo1Area.text = it.area
                    holder.tvAnimeInfo1Year.text = it.year
                    holder.tvAnimeInfo1Index.text =
                        App.context.getString(R.string.anime_detail_index) + it.index
                    holder.tvAnimeInfo1Info.text = it.info
                    holder.flAnimeInfo1Type.removeAllViews()
                    for (i in it.animeType.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeInfo1Type,
                                false
                            ) as TextView
                        tvFlowLayout.text = it.animeType[i].title
                        tvFlowLayout.setOnClickListener { it1 ->
                            if (it.animeType[i].actionUrl.isBlank()) return@setOnClickListener
                            //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                            val actionUrl = it.animeType[i].actionUrl.run {
                                if (endsWith("/")) "${this}${it.animeType[i].title}"
                                else "${this}/${it.animeType[i].title}"
                            }
                            process(
                                activity,
                                Const.ActionUrl.ANIME_CLASSIFY + actionUrl
                            )
                        }
                        holder.flAnimeInfo1Type.addView(tvFlowLayout)
                    }

                    holder.flAnimeInfo1Tag.removeAllViews()
                    for (i in it.tag.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeInfo1Tag,
                                false
                            ) as TextView
                        tvFlowLayout.text = it.tag[i].title
                        tvFlowLayout.setOnClickListener { _ ->
                            //此处是”标签“，由于分类没有这一大项，因此传入”“串
                            val actionUrl = it.tag[i].actionUrl.run {
                                if (endsWith("/")) "${this}${it.tag[i].title}"
                                else "${this}/${it.tag[i].title}"
                            }
                            process(
                                activity,
                                Const.ActionUrl.ANIME_CLASSIFY + actionUrl
                            )
                        }
                        holder.flAnimeInfo1Tag.addView(tvFlowLayout)
                    }

                    //查找番剧播放历史决定是否可续播
                    holder.tvAnimeInfoContinuePlay.apply {
                        gone()
                        getAppDataBase().historyDao()
                            .getHistoryLiveData(activity.getPartUrl())
                            .also {
                                setOnClickListener { v ->
                                    val url = v.tag
                                    if (url is String) {
                                        process(activity, url, url)
                                    }
                                }
                                visible()
                            }
                            //FIX_TODO 2022/1/22 14:53 0 这里没有在打开播放后更新，原因未知，所以暂时只能手动刷新
                            .observe(activity) { hb ->
                                if (hb != null) {
                                    text = "续播 ${hb.lastEpisode}"
                                    tag = hb.lastEpisodeUrl
                                } else
                                //小心复用，所以主要主动隐藏
                                    gone()
                            }
                    }
                }
            }
            holder is AnimeCover1ViewHolder && item is AnimeCoverBean -> {
                holder.ivAnimeCover1Cover.setTag(R.id.image_view_tag, item.cover?.url)
                holder.tvAnimeCover1Title.setTextColor(activity.getResColor(R.color.foreground_white_skin))
                if (holder.ivAnimeCover1Cover.getTag(R.id.image_view_tag) == item.cover?.url) {
                    holder.ivAnimeCover1Cover.loadImage(
                        item.cover?.url ?: "",
                        referer = item.cover?.referer
                    )
                }
                holder.tvAnimeCover1Title.text = item.title
                if (item.episode.isBlank()) {
                    holder.tvAnimeCover1Episode.gone()
                } else {
                    holder.tvAnimeCover1Episode.visible()
                    holder.tvAnimeCover1Episode.text = item.episode
                }
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showEpisodeSheetDialog(dataList: List<AnimeEpisodeDataBean>): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(activity, R.layout.dialog_bottom_sheet_2, null)
        bottomSheetDialog.setContentView(contentView)
        val recyclerView =
            contentView.findViewById<BottomSheetRecyclerView>(R.id.rv_dialog_bottom_sheet_2)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.post {
            recyclerView.setPadding(16.dp, 16.dp, 16.dp, 16.dp)
            recyclerView.scrollToPosition(0)
        }
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(animeEpisodeItemDecoration)
        }
        recyclerView.adapter = EpisodeRecyclerView1Adapter(
            activity,
            dataList,
            bottomSheetDialog,
            showType = 1,
            detailPartUrl = activity.getPartUrl()
        )
        return bottomSheetDialog
    }

    open class EpisodeRecyclerView1Adapter(
        private val activity: Activity,
        private val dataList: List<AnimeEpisodeDataBean>,
        private val dialog: Dialog? = null,
        private val showType: Int = 0,    //0是横向，1是三列
        private val detailPartUrl: String = ""
    ) : BaseRvAdapter(dataList) {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val item = dataList[position]

            when (holder) {
                is AnimeEpisode2ViewHolder -> {
                    holder.tvAnimeEpisode2.text = item.title
                    val layoutParams = holder.itemView.layoutParams
                    holder.itemView.background = if (showType == 0) {
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        if (layoutParams is ViewGroup.MarginLayoutParams) {
                            layoutParams.setMargins(0, 5.dp, 10.dp, 5.dp)
                        }
                        holder.itemView.layoutParams = layoutParams
                        holder.tvAnimeEpisode2.setTextColor(activity.getResColor(R.color.foreground_white_skin))
                        getResDrawable(R.drawable.shape_circle_corner_edge_white_ripper_5_skin)
                    } else {
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        holder.itemView.setPadding(0, 10.dp, 0, 10.dp)
                        holder.itemView.layoutParams = layoutParams
                        holder.tvAnimeEpisode2.setTextColor(activity.getResColor(R.color.foreground_main_color_2_skin))
                        getResDrawable(R.drawable.shape_circle_corner_edge_main_color_2_ripper_5_skin)
                    }
                    holder.itemView.setOnClickListener {
                        val const = DataSourceManager.getConst()
                        if (const != null && item.actionUrl.startsWith(const.actionUrl.ANIME_PLAY()))
                            process(activity, item.actionUrl + detailPartUrl, item.actionUrl)
                        else process(activity, item.actionUrl, item.actionUrl)
                        dialog?.dismiss()
                    }
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
                }
            }
        }
    }
}